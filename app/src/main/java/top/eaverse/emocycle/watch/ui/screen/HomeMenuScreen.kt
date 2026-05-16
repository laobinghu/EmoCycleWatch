package top.eaverse.emocycle.watch.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText

@Composable
fun HomeMenuScreen(
    onRecordNowClick: () -> Unit,
    onViewHistoryClick: () -> Unit
) {
    val scrollState = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = scrollState,
        timeText = { TimeText() }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "greeting") {
                ListHeader {
                    Text("你好，今天感觉怎么样？")
                }
            }

            item(key = "menu_record") {
                Button(
                    onClick = onRecordNowClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("记录现在")
                }
            }

            item(key = "menu_history") {
                Button(
                    onClick = onViewHistoryClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("查看记录")
                }
            }
        }
    }
}
