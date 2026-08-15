package ai.wmetax.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

private val Bg=Color(0xFF050507)
private val Card=Color(0xFF15161B)
private val Input=Color(0xFF17181D)
private val Fg=Color(0xFFF4F4F6)
private val Muted=Color(0xFF8C8D95)
data class Msg(val text:String,val user:Boolean,val model:String="",val auto:Boolean=false)

private const val APP_VERSION="1.0.1"
private const val VERSION_URL="https://raw.githubusercontent.com/farrokh992-eng/wMetax-ai/main/version.json"
private const val APK_URL="https://github.com/farrokh992-eng/wMetax-ai/releases/latest/download/wMetax-ai.apk"

class MainActivity:ComponentActivity(){
    override fun onCreate(b:Bundle?){super.onCreate(b);setContent{WMetaxApp()}}
}

@Composable
fun WMetaxApp(){
    var model by remember{mutableStateOf("Auto")}
    var input by remember{mutableStateOf(TextFieldValue(""))}
    var showChats by remember{mutableStateOf(false)}
    var showSettings by remember{mutableStateOf(false)}
    var menu by remember{mutableStateOf(false)}
    var updateAvailable by remember{mutableStateOf(false)}
    var latestVersion by remember{mutableStateOf("")}
    var checkingUpdate by remember{mutableStateOf(false)}
    val messages=remember{mutableStateListOf<Msg>()}
    val scope=rememberCoroutineScope()
    val context=LocalContext.current

    fun checkForUpdate(){
        if(checkingUpdate)return
        checkingUpdate=true
        scope.launch(Dispatchers.IO){
            try{
                val c=URL(VERSION_URL+"?t="+System.currentTimeMillis()).openConnection() as HttpURLConnection
                c.connectTimeout=7000;c.readTimeout=7000
                val v=JSONObject(c.inputStream.bufferedReader().use{it.readText()}).optString("version","")
                withContext(Dispatchers.Main){
                    latestVersion=v
                    updateAvailable=v.isNotBlank()&&v!=APP_VERSION
                    checkingUpdate=false
                }
                c.disconnect()
            }catch(_:Exception){withContext(Dispatchers.Main){checkingUpdate=false}}
        }
    }

    fun installUpdate(){
        try{
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(APK_URL)).apply{
                setDataAndType(Uri.parse(APK_URL),"application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }catch(_:Exception){
            context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/farrokh992-eng/wMetax-ai/releases/latest")))
        }
    }

    LaunchedEffect(Unit){checkForUpdate()}

    MaterialTheme(colorScheme=darkColorScheme(background=Bg,surface=Card,onBackground=Fg,onSurface=Fg,primary=Fg)){
        Surface(Modifier.fillMaxSize(),color=Bg){
            Column{
                Row(Modifier.fillMaxWidth().height(62.dp).padding(horizontal=14.dp),verticalAlignment=Alignment.CenterVertically){
                    Image(bitmap=BitmapFactory.decodeResource(context.resources, ai.wmetax.app.R.drawable.wmetax_logo).asImageBitmap(),contentDescription="wMetax ai",modifier=Modifier.size(36.dp))
                    Spacer(Modifier.width(9.dp))
                    Text("wMetax ",fontSize=25.sp,color=Fg);Text("ai",fontSize=25.sp,color=Muted)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick={showChats=true}){Icon(Icons.Default.Menu,"Chats",tint=Fg)}
                    IconButton(onClick={showSettings=true}){Icon(Icons.Default.Settings,"Settings",tint=Fg)}
                }
                Box(Modifier.fillMaxWidth().padding(vertical=2.dp),contentAlignment=Alignment.Center){
                    Button(onClick={menu=!menu},shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=Card)){
                        Text(model);Text(" ⌄",color=Muted)
                    }
                    DropdownMenu(expanded=menu,onDismissRequest={menu=false}){
                        listOf("Auto","ChatGPT","Claude","Grok","Gemini","DeepSeek","Perplexity").forEach{n->
                            DropdownMenuItem(text={Text(n)},onClick={model=n;menu=false})
                        }
                    }
                }
                if(updateAvailable){
                    Surface(color=Card,shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=4.dp)){
                        Row(Modifier.fillMaxWidth().padding(horizontal=12.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically){
                            Text("Update available: $latestVersion",color=Fg,modifier=Modifier.weight(1f),fontSize=13.sp)
                            TextButton(onClick={installUpdate()}){Text("UPDATE")}
                        }
                    }
                }
                LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(horizontal=14.dp),verticalArrangement=Arrangement.spacedBy(10.dp),contentPadding=PaddingValues(top=12.dp,bottom=10.dp)){
                    if(messages.isEmpty())item{
                        Box(Modifier.fillMaxWidth().padding(top=100.dp),contentAlignment=Alignment.Center){
                            Column(horizontalAlignment=Alignment.CenterHorizontally){
                                Image(bitmap=BitmapFactory.decodeResource(context.resources, ai.wmetax.app.R.drawable.wmetax_logo).asImageBitmap(),contentDescription=null,modifier=Modifier.size(84.dp))
                                Text("wMetax ai",fontSize=28.sp,color=Fg)
                                Text("Ask anything. Auto will choose the best model.",color=Muted,modifier=Modifier.padding(top=8.dp))
                            }
                        }
                    }
                    items(messages){m->
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=if(m.user)Arrangement.End else Arrangement.Start){
                            Surface(color=if(m.user)Color(0xFF2A2C34) else Card,shape=RoundedCornerShape(17.dp)){
                                Column(Modifier.padding(13.dp).widthIn(max=340.dp)){
                                    Text(m.text,color=Fg,fontSize=15.sp)
                                    if(!m.user)Text(m.model+(if(m.auto)" (Auto)" else " (Manual)"),color=Muted,fontSize=11.sp,modifier=Modifier.padding(top=6.dp))
                                }
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal=14.dp,vertical=10.dp),verticalAlignment=Alignment.Bottom){
                    OutlinedTextField(value=input,onValueChange={input=it},modifier=Modifier.weight(1f),placeholder={Text("Ask anything…",color=Muted)},shape=RoundedCornerShape(18.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=Color.Transparent,unfocusedBorderColor=Color.Transparent,focusedContainerColor=Input,unfocusedContainerColor=Input))
                    Spacer(Modifier.width(7.dp))
                    IconButton(onClick={
                        val t=input.text.trim()
                        if(t.isNotEmpty()){
                            val auto=model=="Auto";val actual=if(auto)"ChatGPT" else model
                            messages.add(Msg(t,true));input=TextFieldValue("")
                            scope.launch{
                                kotlinx.coroutines.delay(300)
                                messages.add(Msg("Gateway هوش مصنوعی هنوز به Provider متصل نشده است؛ رابط و مسیر اتصال آماده شده و اتصال واقعی در مرحله بعد فعال می‌شود.",false,actual,auto))
                            }
                        }
                    },modifier=Modifier.size(52.dp)){Icon(Icons.Default.Send,"Send",tint=Fg)}
                }
            }
            if(showChats)AlertDialog(onDismissRequest={showChats=false},title={Text("Chats")},text={Text("No saved chats yet.")},confirmButton={TextButton(onClick={showChats=false}){Text("Close")}})
            if(showSettings)AlertDialog(onDismissRequest={showSettings=false},title={Text("Settings")},text={
                Column{
                    Text("App language: English")
                    Text("Response language: Auto",modifier=Modifier.padding(top=8.dp))
                    Text("Current version: $APP_VERSION",modifier=Modifier.padding(top=14.dp))
                    TextButton(onClick={checkForUpdate()},modifier=Modifier.padding(top=6.dp)){Text(if(checkingUpdate)"Checking…" else "Check for updates")}
                    if(updateAvailable)TextButton(onClick={installUpdate()}){Text("Update now")}
                }
            },confirmButton={TextButton(onClick={showSettings=false}){Text("Done")}})
        }
    }
}
