package me.zeroeightsix.kami.feature

import com.google.common.base.Converter
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import glm_.vec2.Vec2
import imgui.ImGui.button
import imgui.ImGui.sameLine
import imgui.ImGui.text
import me.zeroeightsix.kami.KamiMod
import me.zeroeightsix.kami.setting.Settings
import me.zeroeightsix.kami.util.Bind
import net.minecraft.client.util.InputUtil

open class ListeningFeature(
    originalName: String = "No name",
    description: String = "No description",
    hidden: Boolean = false,
    _alwaysListening: Boolean = false
) : Feature(originalName, description, hidden),
    Listening {

    var alwaysListening = _alwaysListening
        set(value) {
            field = value
            if (value) KamiMod.EVENT_BUS.subscribe(this)
            else if (!value && isDisabled()) KamiMod.EVENT_BUS.unsubscribe(this)
        }
    val bindSetting = register(
        Settings.custom("Bind", Bind.none(),
            BindConverter(),
            {
                text("Bound to " + getBind().toString()) // TODO: Highlight bind in another color?
                sameLine(0, -1)
                if (button("Bind", Vec2())) { // TODO: Bind popup?
                    // Maybe just display "Press a key" instead of the normal "Bound to ...", and wait for a key press.
                }
            }
        ).build()
    )

    override fun onEnable() {
        if (!alwaysListening) KamiMod.EVENT_BUS.subscribe(this)
    }

    override fun onDisable() {
        if (!alwaysListening) KamiMod.EVENT_BUS.unsubscribe(this)
    }

    override fun isAlwaysListening(): Boolean = alwaysListening

    override fun getBind(): Bind = bindSetting.value

    private class BindConverter : Converter<Bind, JsonElement>() {
        override fun doForward(bind: Bind): JsonElement {
            val array = JsonArray()
            array.add(bind.isAlt)
            array.add(bind.isCtrl)
            array.add(bind.isShift)
            //TODO
            return array
        }

        override fun doBackward(jsonElement: JsonElement): Bind {
            val array = jsonElement.asJsonArray
            val alt = array[0].asBoolean
            val ctrl = array[1].asBoolean
            val shift = array[2].asBoolean
            val key = array[2].asInt
            val scancode = array[3].asInt
            return Bind(ctrl, alt, shift, InputUtil.getKeyCode(key, scancode))
        }
    }

}