package tachiyomi.core.extensions

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

internal object ScriptExtensionJavaScriptBridge {
    fun manifestScript(
        script: String,
        json: Json,
    ): String {
        val encodedScript = json.encodeToString(script)
        return """
            (function () {
                $HELPERS
                var extension = __chimahonLoadExtension($encodedScript);
                return JSON.stringify(__chimahonNormalizeManifest(extension));
            })();
        """.trimIndent()
    }

    fun invocationScript(
        script: String,
        sourceId: Long,
        method: String,
        arguments: JsonObject,
        json: Json,
    ): String {
        val encodedScript = json.encodeToString(script)
        val encodedSourceId = json.encodeToString(sourceId.toString())
        val encodedMethod = json.encodeToString(method)
        val encodedArguments = json.encodeToString(arguments)
        return """
            (function () {
                $HELPERS
                var sourceId = $encodedSourceId;
                var methodName = $encodedMethod;
                var extension = __chimahonLoadExtension($encodedScript);
                var source = __chimahonFindSource(extension, sourceId);
                if (!source) {
                    throw new Error("Extension source not found: " + sourceId);
                }
                var result = __chimahonInvokeOperation(extension, source, methodName, $encodedArguments);
                if (result && typeof result.then === "function") {
                    throw new Error("Async JavaScript methods are not supported; return a request descriptor instead");
                }
                return JSON.stringify(result === undefined ? null : result);
            })();
        """.trimIndent()
    }

    private const val HELPERS = """
        function __chimahonRoot() {
            if (typeof globalThis !== "undefined") return globalThis;
            if (typeof self !== "undefined") return self;
            if (typeof window !== "undefined") return window;
            return this;
        }
        function __chimahonHasValue(value) {
            if (value === undefined || value === null) return false;
            return typeof value !== "string" || value.length > 0;
        }
        function __chimahonIsArray(value) {
            return Object.prototype.toString.call(value) === "[object Array]";
        }
        function __chimahonKeys(value) {
            var keys = [];
            if (!value) return keys;
            for (var key in value) {
                if (Object.prototype.hasOwnProperty.call(value, key)) {
                    keys.push(key);
                }
            }
            return keys;
        }
        function __chimahonIsEmptyObject(value) {
            return value && typeof value === "object" && __chimahonKeys(value).length === 0;
        }
        function __chimahonFirstValue(object, keys) {
            if (!object) return undefined;
            for (var index = 0; index < keys.length; index += 1) {
                var key = keys[index];
                if (key in object) {
                    var value = object[key];
                    if (__chimahonHasValue(value)) return value;
                }
            }
            return undefined;
        }
        function __chimahonFirstString(object, keys) {
            var value = __chimahonFirstValue(object, keys);
            return __chimahonHasValue(value) ? String(value) : "";
        }
        function __chimahonIntValue(value) {
            if (!__chimahonHasValue(value)) return null;
            if (typeof value === "number") {
                return isFinite(value) ? Math.floor(value) : null;
            }
            var parsed = parseInt(String(value), 10);
            return isNaN(parsed) ? null : parsed;
        }
        function __chimahonBoolValue(value, fallback) {
            if (!__chimahonHasValue(value)) return fallback;
            if (typeof value === "boolean") return value;
            if (typeof value === "number") return value !== 0;
            var normalized = String(value).toLowerCase();
            if (normalized === "true" || normalized === "1" || normalized === "yes" || normalized === "y") return true;
            if (normalized === "false" || normalized === "0" || normalized === "no" || normalized === "n") return false;
            return fallback;
        }
        function __chimahonUnwrapDefault(candidate) {
            var seen = 0;
            while (candidate && candidate.default && candidate.default !== candidate && seen < 8) {
                candidate = candidate.default;
                seen += 1;
            }
            return candidate;
        }
        function __chimahonLooksLikeExtension(candidate) {
            candidate = __chimahonUnwrapDefault(candidate);
            if (!candidate) return false;
            return __chimahonHasValue(candidate.manifest) ||
                __chimahonHasValue(candidate.sources) ||
                __chimahonHasValue(candidate.sourceCount) ||
                __chimahonHasValue(candidate.source_count) ||
                __chimahonHasValue(candidate.id) ||
                __chimahonHasValue(candidate.pkg) ||
                __chimahonHasValue(candidate.packageName) ||
                __chimahonHasValue(candidate.pkgName) ||
                __chimahonHasValue(candidate["package"]);
        }
        function __chimahonMaterializeExport(candidate) {
            candidate = __chimahonUnwrapDefault(candidate);
            if (!candidate) return null;
            if (typeof candidate === "function") {
                if (__chimahonLooksLikeExtension(candidate)) return candidate;
                try {
                    var constructed = new candidate();
                    if (__chimahonLooksLikeExtension(constructed)) return __chimahonUnwrapDefault(constructed);
                } catch (ignoredConstructorError) {
                }
                try {
                    var returned = candidate();
                    if (__chimahonLooksLikeExtension(returned)) return __chimahonUnwrapDefault(returned);
                } catch (ignoredFactoryError) {
                }
                return candidate;
            }
            if (candidate.createExtension && typeof candidate.createExtension === "function") {
                try {
                    var created = candidate.createExtension();
                    if (__chimahonLooksLikeExtension(created)) return __chimahonUnwrapDefault(created);
                } catch (ignoredCreateError) {
                }
            }
            if (candidate.extension && __chimahonLooksLikeExtension(candidate.extension)) {
                return __chimahonUnwrapDefault(candidate.extension);
            }
            return candidate;
        }
        function __chimahonTransformModuleSyntax(source) {
            return String(source)
                .replace(/export\s+default\s+/g, "module.exports = ")
                .replace(/export\s+(var|let|const)\s+chimahonExtension\s*=/g, "var chimahonExtension =")
                .replace(/export\s+(var|let|const)\s+extension\s*=/g, "var extension =");
        }
        function __chimahonLoadExtension(encodedScript) {
            var root = __chimahonRoot();
            var module = { exports: {} };
            var exports = module.exports;
            try {
                eval(encodedScript);
            } catch (scriptError) {
                var transformedScript = __chimahonTransformModuleSyntax(encodedScript);
                if (transformedScript === encodedScript) {
                    throw scriptError;
                }
                eval(transformedScript);
            }

            var candidates = [module.exports, exports];
            if (typeof chimahonExtension !== "undefined") candidates.push(chimahonExtension);
            if (typeof extension !== "undefined") candidates.push(extension);
            candidates.push(root.chimahonExtension);
            candidates.push(root.ChimahonExtension);
            candidates.push(root.extension);
            candidates.push(root.default);

            for (var index = 0; index < candidates.length; index += 1) {
                var candidate = __chimahonMaterializeExport(candidates[index]);
                if (__chimahonLooksLikeExtension(candidate) && !__chimahonIsEmptyObject(candidate)) {
                    return candidate;
                }
            }
            throw new Error("Extension did not export a manifest");
        }
        function __chimahonMaterializeSource(candidate) {
            candidate = __chimahonUnwrapDefault(candidate);
            if (!candidate) return candidate;
            if (typeof candidate === "function") {
                try {
                    return __chimahonUnwrapDefault(new candidate());
                } catch (ignoredConstructorError) {
                }
                try {
                    return __chimahonUnwrapDefault(candidate());
                } catch (ignoredFactoryError) {
                }
            }
            return candidate;
        }
        function __chimahonSourceEntries(rawSources) {
            var sources = rawSources;
            if (typeof sources === "function") {
                sources = sources();
            }
            var entries = [];
            if (!sources) return entries;
            if (__chimahonIsArray(sources)) {
                for (var index = 0; index < sources.length; index += 1) {
                    entries.push({ key: String(index), value: sources[index] });
                }
                return entries;
            }
            if (typeof sources === "object") {
                var keys = __chimahonKeys(sources);
                for (var keyIndex = 0; keyIndex < keys.length; keyIndex += 1) {
                    var key = keys[keyIndex];
                    entries.push({ key: key, value: sources[key] });
                }
            }
            return entries;
        }
        function __chimahonNormalizeSource(rawSource, key, rawManifest) {
            var source = __chimahonMaterializeSource(rawSource);
            if (!source) return null;
            var id = __chimahonIntValue(__chimahonFirstValue(source, ["id", "sourceId", "source_id"]));
            if (id === null) id = __chimahonIntValue(key);
            var language = __chimahonFirstString(source, ["language", "lang"]);
            if (!language) language = __chimahonFirstString(rawManifest, ["language", "lang"]);
            var baseUrl = __chimahonFirstString(source, ["baseUrl", "base_url", "url", "host"]);
            if (!baseUrl) baseUrl = __chimahonFirstString(rawManifest, ["baseUrl", "base_url"]);
            return {
                id: id,
                name: __chimahonFirstString(source, ["name", "sourceName", "source_name"]) ||
                    __chimahonFirstString(rawManifest, ["sourceName", "source_name", "name"]),
                language: language || "all",
                baseUrl: baseUrl,
                isNsfw: __chimahonBoolValue(
                    __chimahonFirstValue(source, ["isNsfw", "is_nsfw", "nsfw"]),
                    __chimahonBoolValue(__chimahonFirstValue(rawManifest, ["isNsfw", "is_nsfw", "nsfw"]), false)
                ),
                supportsLatest: __chimahonBoolValue(
                    __chimahonFirstValue(source, ["supportsLatest", "supports_latest", "supportsLatestUpdates"]),
                    false
                )
            };
        }
        function __chimahonNormalizeSources(rawSources, rawManifest) {
            var entries = __chimahonSourceEntries(rawSources);
            var normalized = [];
            for (var index = 0; index < entries.length; index += 1) {
                var source = __chimahonNormalizeSource(entries[index].value, entries[index].key, rawManifest);
                if (source) normalized.push(source);
            }
            return normalized;
        }
        function __chimahonCommonLanguage(sources) {
            if (!sources || sources.length === 0) return "";
            var language = sources[0].language || "";
            for (var index = 1; index < sources.length; index += 1) {
                if ((sources[index].language || "") !== language) return "";
            }
            return language;
        }
        function __chimahonAnyNsfw(sources) {
            for (var index = 0; index < sources.length; index += 1) {
                if (sources[index].isNsfw) return true;
            }
            return false;
        }
        function __chimahonPackageType(value) {
            var normalized = __chimahonHasValue(value) ? String(value).toLowerCase() : "";
            return normalized === "androidapk" || normalized === "android_apk" ||
                normalized === "android" || normalized === "apk" ? "AndroidApk" : "JavaScript";
        }
        function __chimahonNormalizeManifest(extension) {
            var rawManifest = __chimahonMaterializeSource(extension.manifest) || extension;
            var rawSources = __chimahonFirstValue(rawManifest, ["sources"]);
            if (!__chimahonHasValue(rawSources)) {
                rawSources = __chimahonFirstValue(extension, ["sources"]);
            }
            var sources = __chimahonNormalizeSources(rawSources, rawManifest);
            var id = __chimahonFirstString(rawManifest, ["id", "packageName", "pkgName", "pkg", "package"]);
            if (!id) id = __chimahonFirstString(extension, ["id", "packageName", "pkgName", "pkg", "package"]);
            var packageName = __chimahonFirstString(rawManifest, ["packageName", "pkgName", "pkg", "package"]);
            if (!packageName) packageName = __chimahonFirstString(extension, ["packageName", "pkgName", "pkg", "package"]) || id;
            var sourceCount = __chimahonIntValue(__chimahonFirstValue(rawManifest, ["sourceCount", "source_count"]));
            if (sourceCount === null) {
                sourceCount = __chimahonIntValue(__chimahonFirstValue(extension, ["sourceCount", "source_count"]));
            }
            if (sourceCount === null || sourceCount < sources.length) sourceCount = sources.length;
            var language = __chimahonFirstString(rawManifest, ["language", "lang"]);
            if (!language) language = __chimahonFirstString(extension, ["language", "lang"]);
            if (!language) language = __chimahonCommonLanguage(sources);
            return {
                id: id,
                name: __chimahonFirstString(rawManifest, ["name", "displayName", "display_name"]) ||
                    __chimahonFirstString(extension, ["name", "displayName", "display_name"]),
                version: __chimahonFirstString(rawManifest, ["version", "versionName", "version_name", "versionCode", "code"]) ||
                    __chimahonFirstString(extension, ["version", "versionName", "version_name", "versionCode", "code"]),
                sources: sources,
                packageName: packageName,
                packageType: __chimahonPackageType(__chimahonFirstValue(rawManifest, ["packageType", "package_type", "type"])),
                language: language,
                sourceCount: sourceCount,
                isNsfw: __chimahonBoolValue(
                    __chimahonFirstValue(rawManifest, ["isNsfw", "is_nsfw", "nsfw"]),
                    __chimahonBoolValue(__chimahonFirstValue(extension, ["isNsfw", "is_nsfw", "nsfw"]), __chimahonAnyNsfw(sources))
                ),
                apkName: __chimahonFirstString(rawManifest, ["apkName", "apk_name", "apk"]) || null,
                artifactUrl: __chimahonFirstString(rawManifest, ["artifactUrl", "artifact_url", "downloadUrl", "download_url", "scriptUrl", "script_url", "url"]) || null,
                iconUrl: __chimahonFirstString(rawManifest, ["iconUrl", "icon_url", "icon"]) || null
            };
        }
        function __chimahonSourceMatches(source, sourceId) {
            if (!source) return false;
            var id = __chimahonFirstValue(source, ["id", "sourceId", "source_id"]);
            return __chimahonHasValue(id) && String(id) === String(sourceId);
        }
        function __chimahonFindSource(extension, sourceId) {
            if (extension.getSource && typeof extension.getSource === "function") {
                try {
                    var direct = __chimahonMaterializeSource(extension.getSource(sourceId));
                    if (direct) return direct;
                } catch (ignoredGetSourceError) {
                }
            }
            var rawSources = __chimahonFirstValue(extension, ["sources"]);
            if (!__chimahonHasValue(rawSources) && extension.manifest) {
                rawSources = __chimahonFirstValue(extension.manifest, ["sources"]);
            }
            if (typeof rawSources === "function") {
                rawSources = rawSources.call(extension);
            }
            if (!rawSources) return null;
            if (__chimahonIsArray(rawSources)) {
                for (var arrayIndex = 0; arrayIndex < rawSources.length; arrayIndex += 1) {
                    var arraySource = __chimahonMaterializeSource(rawSources[arrayIndex]);
                    if (__chimahonSourceMatches(arraySource, sourceId)) return arraySource;
                }
                return null;
            }
            if (typeof rawSources === "object") {
                if (String(sourceId) in rawSources) {
                    return __chimahonMaterializeSource(rawSources[String(sourceId)]);
                }
                var keys = __chimahonKeys(rawSources);
                for (var keyIndex = 0; keyIndex < keys.length; keyIndex += 1) {
                    var key = keys[keyIndex];
                    var mappedSource = __chimahonMaterializeSource(rawSources[key]);
                    if (String(key) === String(sourceId) || __chimahonSourceMatches(mappedSource, sourceId)) {
                        return mappedSource;
                    }
                }
            }
            return null;
        }
        function __chimahonOperationHolder(extension, source, methodName) {
            if (source && typeof source[methodName] === "function") {
                return { target: source, operation: source[methodName] };
            }
            if (source && source.methods && typeof source.methods[methodName] === "function") {
                return { target: source, operation: source.methods[methodName] };
            }
            if (extension && typeof extension[methodName] === "function") {
                return { target: extension, operation: extension[methodName] };
            }
            throw new Error("Source method not found: " + methodName);
        }
        function __chimahonInvokeOperation(extension, source, methodName, args) {
            var holder = __chimahonOperationHolder(extension, source, methodName);
            var operation = holder.operation;
            if (operation.length >= 3) {
                return operation.call(holder.target, args, source, extension);
            }
            if (operation.length >= 2) {
                return operation.call(holder.target, args, source);
            }
            return operation.call(holder.target, args);
        }
    """
}
