package app.chimahon.shared.trackingui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun ChimahonTrackerAccountSection(
    accounts: List<ChimahonTrackerAccountUiModel>,
    callbacks: ChimahonTrackingCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChimahonTrackingSectionHeader(
            title = "Trackers",
            subtitle = "${accounts.count { it.accountStatus.isSignedIn }} connected",
        )
        if (accounts.isEmpty()) {
            ChimahonTrackingInlineEmptyState(
                title = "No trackers available",
                message = "Add a tracker service to sync manga, anime, and light novel progress.",
            )
        } else {
            accounts.forEach { account ->
                ChimahonTrackerAccountRow(
                    account = account,
                    callbacks = callbacks,
                )
            }
        }
    }
}

@Composable
fun ChimahonTrackerAccountRow(
    account: ChimahonTrackerAccountUiModel,
    callbacks: ChimahonTrackingCallbacks,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { callbacks.onOpenAccountSettings(account) },
        color = MaterialTheme.colors.surface,
        elevation = 0.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ChimahonTrackerAvatar(account)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (account.isDefault) {
                        Spacer(Modifier.width(8.dp))
                        ChimahonTrackingChip(text = "Default")
                    }
                }
                Text(
                    text = account.displayName,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.64f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    ChimahonTrackingStatusChip(
                        text = account.accountStatus.label,
                        tone = account.accountStatus.statusTone(),
                    )
                    ChimahonTrackingStatusChip(
                        text = account.syncStatus.label,
                        tone = account.syncStatus.statusTone(),
                    )
                    account.supportedMediaKinds.forEach { kind ->
                        ChimahonTrackingMediaBadge(kind)
                    }
                    if (account.privateTrackingEnabled) {
                        ChimahonTrackingStatusChip(text = "Private", tone = ChimahonTrackingTone.Muted)
                    }
                    account.lastSyncLabel?.let {
                        ChimahonTrackingStatusChip(text = it, tone = ChimahonTrackingTone.Muted)
                    }
                }
                account.errorMessage?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(top = 6.dp),
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.error,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                if (account.accountStatus.isSignedIn) {
                    IconButton(onClick = { callbacks.onSyncAccount(account) }) {
                        Icon(
                            imageVector = ChimahonTrackingIcon.Sync.imageVector(),
                            contentDescription = "Sync ${account.name}",
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                    TextButton(onClick = { callbacks.onLogout(account) }) {
                        Text("Logout")
                    }
                } else {
                    Button(onClick = { callbacks.onLogin(account) }) {
                        Text("Login")
                    }
                }
            }
        }
    }
}

@Composable
private fun ChimahonTrackerAvatar(
    account: ChimahonTrackerAccountUiModel,
    modifier: Modifier = Modifier,
) {
    val tone = account.accountStatus.statusTone()
    val colors = tone.colors()
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colors.background),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = account.accountStatus.icon(),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = colors.content,
        )
    }
}

@Composable
fun ChimahonTrackingMediaBadge(
    kind: ChimahonTrackingMediaKind,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.06f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = kind.icon(),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.62f),
        )
        Text(
            text = kind.label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.72f),
        )
    }
}

@Composable
fun ChimahonTrackingChip(
    text: String,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colors.primary,
) {
    Text(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(contentColor.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        style = MaterialTheme.typography.caption,
        color = contentColor,
        fontWeight = FontWeight.SemiBold,
    )
}
