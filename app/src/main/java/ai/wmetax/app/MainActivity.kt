package ai.wmetax.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF090A0D)
private val Surface = Color(0xFF121419)
private val Surface2 = Color(0xFF191B22)
private val Text = Color(0xFFF3F4F6)
private val Muted = Color(0xFF9297A3)
private val Accent = Color(0xFFE7EAF0)

data class AiModel(
    val provider: String,
    val version: String,
    val key: String
)

private val models = listOf(
    AiModel("ChatGPT", "GPT-5.6 / latest", "openai"),
    AiModel("Claude", "latest", "anthropic"),
    AiModel("Grok", "latest", "xai"),
    AiModel("DeepSeek", "V4-Flash", "deepseek"),
    AiModel("Perplexity", "Sonar / latest", "perplexity"),
    AiModel("Gemini", "3.6 Flash", "gemini")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { WMetaxApp() }
    }
}

@Composable
fun WMetaxApp() {
    var selected by remember { mutableStateOf<AiModel?>(null) }
    var pickerOpen by remember { mutableStateOf(false) }
    var prompt by remember { mutableStateOf("") }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Bg,
            surface = Surface,
            primary = Accent,
            onBackground = Text,
            onSurface = Text
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Bg)
                .imePadding()
        ) {
            Column(Modifier.fillMaxSize()) {
                TopBar()

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        Welcome()
                    }
                    item {
                        DemoAssistantMessage(
                            provider = selected?.provider ?: "ChatGPT",
                            version = selected?.version ?: "Auto",
                            manual = selected != null
                        )
                    }
                }

                // Model selector: intentionally above the composer and below the chat.
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    if (pickerOpen) {
                        ModelPicker(
                            selected = selected,
                            onSelect = {
                                selected = it
                                pickerOpen = false
                            }
                        )
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { pickerOpen = !pickerOpen },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Surface2,
                                contentColor = Text
                            ),
                            border = null
                        ) {
                            Text(
                                if (selected == null) "Auto" else "${selected!!.provider}  ${selected!!.version}",
                                fontSize = 13.sp
                            )
                            Spacer(Modifier.width(5.dp))
                            Text("⌄", color = Muted)
                        }
                    }

                    // Ad placeholder. Real ad SDK is intentionally not bundled yet.
                    Box(
                        Modifier.fillMaxWidth().height(48.dp)
                            .background(Surface, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ADVERTISEMENT", color = Muted, fontSize = 10.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Surface2, RoundedCornerShape(20.dp))
                            .padding(start = 16.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = prompt,
                            onValueChange = { prompt = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("پیام خود را بنویسید...", color = Muted) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Text,
                                unfocusedTextColor = Text
                            ),
                            maxLines = 5
                        )
                        TextButton(onClick = { /* wired to backend in next build */ }) {
                            Text("➤", fontSize = 22.sp)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun TopBar() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("wMetax", color = Text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(" ai", color = Muted, fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.weight(1f))
        Text("⋮", color = Text, fontSize = 24.sp)
    }
}

@Composable
private fun Welcome() {
    Column(Modifier.fillMaxWidth().padding(top = 48.dp)) {
        Text("wMetax ai", color = Text, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "هر چیزی می‌خواهید بپرسید؛ مدل مناسب را انتخاب کنید یا بگذارید Auto تصمیم بگیرد.",
            color = Muted,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun DemoAssistantMessage(provider: String, version: String, manual: Boolean) {
    Column(
        Modifier.fillMaxWidth()
            .background(Surface, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Text(
            "نمونه رابط پاسخ",
            color = Text,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "پاسخ مدل در این قسمت به‌صورت Streaming نمایش داده می‌شود. اتصال واقعی AI، Web Research و فایل‌ها در لایه Backend انجام خواهد شد.",
            color = Text,
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("♡", color = Muted)
            Spacer(Modifier.width(16.dp))
            Text("☹", color = Muted)
            Spacer(Modifier.width(16.dp))
            Text("⋮", color = Muted)
            Spacer(Modifier.weight(1f))
            Text(
                "$provider (${if (manual) "Manual" else "Auto"})",
                color = Text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.width(5.dp))
            Text(version, color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ModelPicker(
    selected: AiModel?,
    onSelect: (AiModel?) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(18.dp))
            .padding(vertical = 6.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().clickable { onSelect(null) }.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Auto", color = Text, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text("بهترین مدل + بهترین سرچ", color = Muted, fontSize = 11.sp)
        }

        models.forEach { model ->
            Row(
                Modifier.fillMaxWidth().clickable { onSelect(model) }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(model.provider, color = Text, fontWeight = FontWeight.Medium)
                    Text(model.version, color = Muted, fontSize = 11.sp)
                }
                if (selected?.key == model.key) Text("✓", color = Text)
            }
        }
    }
}
