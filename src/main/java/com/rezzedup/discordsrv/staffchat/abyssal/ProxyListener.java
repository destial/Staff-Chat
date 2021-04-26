package com.rezzedup.discordsrv.staffchat.abyssal;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.rezzedup.discordsrv.staffchat.StaffChatPlugin;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProxyListener implements PluginMessageListener {
    private static ProxyListener proxyListener;
    public ProxyListener() {
        proxyListener = this;
        StaffChatPlugin.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(StaffChatPlugin.getInstance(), StaffChatPlugin.PLUGIN_CHANNEL);
        StaffChatPlugin.getInstance().getServer().getMessenger().registerIncomingPluginChannel(StaffChatPlugin.getInstance(), StaffChatPlugin.PLUGIN_CHANNEL, this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] bytes) {
        if (!channel.equals(StaffChatPlugin.PLUGIN_CHANNEL)) return;

        ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
        String subChannel = input.readUTF();

        if (!subChannel.equalsIgnoreCase("discord")) return;
        String playerName = input.readUTF();
        String message = input.readUTF();

        StaffChatPlugin.getInstance().submitMessageFromInGame(player, message);
    }

    public void sendPluginMessageFromInGame(Player player, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("discord");
            out.writeUTF(message);
            player.sendPluginMessage(StaffChatPlugin.getInstance(), StaffChatPlugin.PLUGIN_CHANNEL, stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPluginMessageFromDiscord(User user, Message message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF("discord");
            out.writeUTF(user.getName());
            out.writeUTF(message.getContentDisplay());
            StaffChatPlugin.getInstance().getServer().sendPluginMessage(StaffChatPlugin.getInstance(), StaffChatPlugin.PLUGIN_CHANNEL, stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProxyListener get() {
        return proxyListener;
    }
}
