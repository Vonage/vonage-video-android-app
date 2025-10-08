import com.vonage.android.chat.ChatFeature
import com.vonage.android.chat.DisabledChatFeature
import com.vonage.android.chat.DisabledChatSignalPlugin
import com.vonage.android.kotlin.signal.ChatSignalPlugin

object ChatModule {
    
    fun getChatFeature(): ChatFeature {
        return DisabledChatFeature()
    }

    fun getPlugin(): ChatSignalPlugin {
        return DisabledChatSignalPlugin()
    }
}