package top.eaverse.emocycle.watch.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import top.eaverse.emocycle.watch.BuildConfig

@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberTransformingLazyColumnState()

    ScreenScaffold(
        scrollState = scrollState,
        timeText = { TimeText() },
        edgeButton = {
            EdgeButton(
                onClick = onBack,
                buttonSize = EdgeButtonSize.Small
            ) {
                Text("返回")
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "header") {
                ListHeader {
                    Text("关于")
                }
            }

            item(key = "name") {
                AboutItem("软件名称", "情绪周期仪")
            }

            item(key = "build_type") {
                AboutItem("构建类型", BuildConfig.BUILD_TYPE)
            }

            item(key = "version") {
                AboutItem("版本号", "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            }

            item(key = "author") {
                AboutItem("作者", "烧瑚烙饼")
            }

            item(key = "opensource") {
                AboutItem("GitHub", "laobinghu/EmoCycleWatch")
            }
        }
    }
}

@Composable
private fun AboutItem(
    label: String,
    value: String
) {
    Text(
        text = "$label：$value",
        modifier = Modifier.fillMaxWidth()
    )
}
