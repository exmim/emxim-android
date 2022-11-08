package com.skipper.exmim_lib.pojo

import com.google.gson.Gson
import com.skipper.exmim_lib.pojo.ContentType.Companion.generateContentType
import com.skipper.exmim_lib.pojo.MessageType.Companion.generateMessageType
import info.mqtt.android.service.QoS
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.properties.Delegates

enum class MessageType(val type: Int) {
    CHAT(1),
    GROUP_CHAT(2),
    SYS(3);
    companion object {
        fun generateMessageType(type: Int): MessageType {
            return when(type) {
                1 -> {
                    CHAT
                }
                else -> {
                    GROUP_CHAT
                }
            }
        }
    }
}

enum class ContentType(val type: Int) {
    TXT(1),
    PIC(2),
    VOICE(3);
    companion object {
        fun generateContentType(type: Int): ContentType {
            return when(type) {
                1 -> {
                    TXT
                }
                else -> {
                    TXT
                }
            }
        }
    }
}


class ImMessage {
    private var messageType by Delegates.notNull<Int>()

    private var contentType by Delegates.notNull<Int>()

    private var body by Delegates.notNull<ImBody>()

    private var from: ImFrom? = null

    private var at: AtOptions? = null

    private var createAt by Delegates.notNull<Long>()

    private var id: String = ""
        get() {
            if (field.isEmpty()) {
                field = UUID.randomUUID().toString()
                return field
            }
            return field
        }

    companion object {
        fun obtainTxtMessage(txt: String, sendUser: User): ImMessage {
            return Builder()
                .messageType(MessageType.CHAT)
                .contentType(ContentType.TXT)
                .body(TxtBody(txt))
                .from(SingleFrom.obtain(sendUser))
                .build()
        }

        fun obtainTextGroupMessage(txt: String, fromGroup: Group, sendUser: User, atOptions: AtOptions?): ImMessage {
            val builder = Builder()
            if (atOptions != null) {
                builder.at(atOptions)
            }
            return builder
                .messageType(MessageType.GROUP_CHAT)
                .contentType(ContentType.TXT)
                .body(TxtBody(txt))
                .from(GroupFrom.obtain(fromGroup, sendUser))
                .build()
        }
    }

    class Builder {

        private var messageType by Delegates.notNull<MessageType>()

        private var contentType by Delegates.notNull<ContentType>()

        private var body by Delegates.notNull<ImBody>()

        private var from: ImFrom? = null

        private var at: AtOptions? = null

        private var createAt: Long? = null

        fun messageType(messageType: MessageType): Builder {
            this.messageType = messageType
            return this
        }

        fun contentType(contentType: ContentType): Builder {
            this.contentType = contentType
            return this
        }

        fun body(body: ImBody): Builder {
            this.body = body
            return this
        }

        fun from(from: ImFrom): Builder {
            this.from = from
            return this
        }

        fun at(at: AtOptions): Builder {
            this.at = at
            return this
        }

        fun createAt(createAt: Long): Builder{
            this.createAt = createAt
            return this
        }

        fun build(id: String = ""): ImMessage {
            val imMessage = ImMessage()
            imMessage.contentType = contentType.type
            imMessage.messageType = messageType.type
            imMessage.body = body
            imMessage.from = from
            if (at != null) {
                imMessage.at = at
            }
            val tempCreateAt = createAt
            if (tempCreateAt != null) {
                imMessage.createAt = tempCreateAt
            } else {
                imMessage.createAt = System.currentTimeMillis()
            }
            if (!id.isEmpty()) {
                imMessage.id = id
            }
            return imMessage
        }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun saveToDb() {

    }
}

interface ImBody

interface ImFrom

data class AtOptions(
    val atMobiles: List<String>,
    val atUserIds: List<String>,
    val isAtAll: Boolean
)

data class TxtBody(
    val content: String
) : ImBody

data class SingleFrom(
    val userId: String,
    val avatar: String,
    val nickName: String
) : ImFrom {
    companion object {
        fun obtain(user: User): SingleFrom {
            return SingleFrom(user.userId, user.avatar, user.nickName)
        }
    }
}

data class GroupFrom(
    val groupId: String,
    val groupName: String,
    val groupLogo: String?,
    val userId: String,
    val avatar: String,
    val nickName: String
) : ImFrom {
    companion object {
        fun obtain(group: Group, user: User): GroupFrom {
            return GroupFrom(
                groupId = group.groupId,
                groupLogo = group.groupLogo,
                groupName = group.groupName,
                userId = user.userId,
                avatar = user.avatar,
                nickName = user.nickName
            )
        }
    }
}

fun ImMessage.obtain(qos: QoS = QoS.AtLeastOnce, retain: Boolean = true): MqttMessage {
    val mqttMessage = MqttMessage()
    mqttMessage.qos = qos.value
    mqttMessage.payload = this.toString().encodeToByteArray()
    mqttMessage.isRetained = retain
    return mqttMessage
}

@Throws(IllegalArgumentException::class)
fun MqttMessage.obtain(): ImMessage {
    val body = String(payload)
    val temp = Gson().fromJson(body, Map::class.java)
    // render from
    val messageMap = temp as Map<String, Any>
    val messageBuilder = ImMessage.Builder()
    val messageTypeNumber = messageMap["messageType"] as Number
    val messageType = generateMessageType(messageTypeNumber.toInt())
    messageBuilder.messageType(messageType)
    when(messageType) {
        MessageType.CHAT -> {
            val fromTemp = messageMap["from"] as Map<String, Any>
            val from = SingleFrom(
                fromTemp["userId"] as String,
                fromTemp["avatar"] as String,
                fromTemp["nickName"] as String
            )
            messageBuilder.from(from)
        }
        MessageType.GROUP_CHAT -> {
            val fromTemp = messageMap["from"] as Map<String, Any>
            val from = GroupFrom(
                fromTemp["groupId"] as String,
                fromTemp["groupName"] as String,
                fromTemp["groupLogo"] as String,
                fromTemp["userId"] as String,
                fromTemp["avatar"] as String,
                fromTemp["nickName"] as String
            )
            messageBuilder.from(from)
            if (messageMap.containsKey("at")) {
                val atTemp = messageMap["at"] as Map<String, Any>
                val atOptions = AtOptions(
                    atMobiles = atTemp["atMobiles"] as List<String>,
                    atUserIds = atTemp["atUserIds"] as List<String>,
                    isAtAll = atTemp["atMobiles"] as Boolean
                )
                messageBuilder.at(atOptions)
            }
        }
        MessageType.SYS -> {}
        else -> {}
    }
    // render body
    val contentTypeNumber = messageMap["contentType"] as Number
    val contentType = generateContentType(contentTypeNumber.toInt())
    messageBuilder.contentType(contentType)
    when (contentType) {
        ContentType.TXT -> {
            val body = messageMap["body"] as Map<String, Any>
            val txt = body["content"] as String
            messageBuilder.body(TxtBody(txt))
        }
    }
    val createAt = messageMap["createAt"] as Number
    val messageId = messageMap["id"] as String
    return messageBuilder.createAt(createAt.toLong())
        .build(messageId)
}