package app.chimahon.shared.reader.appbars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

private val readerBarsSlideAnimationSpec = tween<IntOffset>(200)
private val readerBarsFadeAnimationSpec = tween<Float>(150)

@Composable
fun ReaderAppBars(
    state: ReaderAppBarsState,
    topBarActions: ReaderTopBarActions,
    bottomBarActions: ReaderBottomBarActions,
    navigatorActions: ReaderChapterNavigatorActions,
    modifier: Modifier = Modifier,
    colors: ReaderAppBarColors = ReaderAppBarDefaults.colors(),
) {
    Column(modifier = modifier.fillMaxHeight()) {
        AnimatedVisibility(
            visible = state.visible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = readerBarsSlideAnimationSpec,
            ) + fadeIn(animationSpec = readerBarsFadeAnimationSpec),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = readerBarsSlideAnimationSpec,
            ) + fadeOut(animationSpec = readerBarsFadeAnimationSpec),
        ) {
            ReaderTopBar(
                state = state.topBar,
                actions = topBarActions,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.surface),
                colors = colors,
            )
        }

        when (state.navBarType) {
            ReaderNavigationBarType.VerticalLeft -> {
                AnimatedVisibility(
                    visible = state.visible,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) + fadeIn(animationSpec = readerBarsFadeAnimationSpec),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) + fadeOut(animationSpec = readerBarsFadeAnimationSpec),
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Start),
                ) {
                    ReaderVerticalChapterNavigator(
                        state = state.navigator,
                        actions = navigatorActions,
                        colors = colors,
                    )
                }
            }
            ReaderNavigationBarType.VerticalRight -> {
                AnimatedVisibility(
                    visible = state.visible,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) + fadeIn(animationSpec = readerBarsFadeAnimationSpec),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = readerBarsSlideAnimationSpec,
                    ) + fadeOut(animationSpec = readerBarsFadeAnimationSpec),
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.End),
                ) {
                    ReaderVerticalChapterNavigator(
                        state = state.navigator,
                        actions = navigatorActions,
                        colors = colors,
                    )
                }
            }
            ReaderNavigationBarType.Bottom -> Spacer(modifier = Modifier.weight(1f))
        }

        AnimatedVisibility(
            visible = state.visible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = readerBarsSlideAnimationSpec,
            ) + fadeIn(animationSpec = readerBarsFadeAnimationSpec),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = readerBarsSlideAnimationSpec,
            ) + fadeOut(animationSpec = readerBarsFadeAnimationSpec),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (state.navBarType == ReaderNavigationBarType.Bottom) {
                    ReaderHorizontalChapterNavigator(
                        state = state.navigator,
                        actions = navigatorActions,
                        colors = colors,
                    )
                }
                ReaderBottomBar(
                    state = state.bottomBar,
                    actions = bottomBarActions,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp),
                    colors = colors,
                )
            }
        }
    }
}
