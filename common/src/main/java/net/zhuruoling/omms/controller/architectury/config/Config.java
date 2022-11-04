package net.zhuruoling.omms.controller.architectury.config;

import com.mojang.logging.LogUtils;
import net.zhuruoling.omms.controller.architectury.OMMSControllerExpectPlatform;
import net.zhuruoling.omms.controller.architectury.util.Util;
import org.slf4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Properties;

public class Config {
    public static final String DEFAULT_CONFIG = """
            #OMMS config
            enableWhitelist=false
            enableChatBridge=false
            httpQueryAddr=localhost
            httpQueryPort=50001
            controllerName=omms-controller
            usesWhitelist=my_whitelist
            channel=GLOBAL
            serverMappings""".trim();
    private final Logger logger = LogUtils.getLogger();
    private boolean enableWhitelist = false;
    private boolean enableChatBridge = false;
    private String controllerName;
    private String whitelistName;
    private String chatChannel;
    private String httpQueryAddr;
    private int httpQueryPort;
    private HashMap<String, ServerMapping> serverMappings;

    public Config load() {
        Path configPath = Path.of(Util.joinFilePaths(OMMSControllerExpectPlatform.getConfigDirectory().toAbsolutePath().toString(), "omms.properties"));
        if (!Files.exists(configPath)) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(configPath.toFile());
                fileOutputStream.write(DEFAULT_CONFIG.getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (Exception e) {
                logger.error("Cannot create config \"omms.properties\".", e);
            }
        }
        if (Files.exists(configPath)) {
            try {
                FileInputStream fileInputStream = new FileInputStream(configPath.toFile());
                Properties properties = new Properties();
                properties.load(fileInputStream);
                enableWhitelist = (Boolean) properties.getOrDefault("enableWhitelist", false);
                enableChatBridge = (Boolean) properties.getOrDefault("enableChatBridge", false);
                controllerName = (String) properties.getOrDefault("controllerName", "omms-controller");
                whitelistName = (String) properties.getOrDefault("usesWhitelist", "my_whitelist");
                httpQueryPort = (Integer) properties.getOrDefault("httpQueryPort", 50001);
                httpQueryAddr = (String) properties.getOrDefault("httpQueryAddr", "localhost");
                chatChannel = (String) properties.getOrDefault("chatChannel", "GLOBAL");
                String serverMappingNames = (String) properties.getOrDefault("serverMappings", "");
                if (serverMappingNames.isEmpty()) {
                    return this;
                }
                if (serverMappingNames.contains(",")) {
                    if (serverMappingNames.isBlank()) {
                        return this;
                    }
                    HashMap<String, ServerMapping> map = new HashMap<>();
                    for (String name : serverMappingNames.split(",")) {
                        if (name.isBlank()) {
                            continue;
                        }
                        ServerMapping mapping = new ServerMapping();
                        mapping.setWhitelistName(serverMappingNames);
                        String displayName = (String) properties.getOrDefault("serverMapping.%s.displayName".formatted(name), "");
                        String proxyName = (String) properties.getOrDefault("serverMapping.%s.proxyName".formatted(name), "");
                        if (displayName.isBlank() || proxyName.isBlank()) {
                            setServerMappings(null);
                            continue;
                        }
                        mapping.setDisplayName(displayName);
                        mapping.setProxyName(proxyName);
                        map.put(name, mapping);
                    }
                    setServerMappings(map);
                } else {
                    ServerMapping mapping = new ServerMapping();
                    mapping.setWhitelistName(serverMappingNames);
                    String displayName = (String) properties.getOrDefault("serverMapping.%s.displayName".formatted(serverMappingNames), "");
                    String proxyName = (String) properties.getOrDefault("serverMapping.%s.proxyName".formatted(serverMappingNames), "");
                    if (displayName.isBlank() || proxyName.isBlank()) {
                        setServerMappings(null);
                        return this;
                    }
                    mapping.setDisplayName(displayName);
                    mapping.setProxyName(proxyName);
                    HashMap<String, ServerMapping> hashMap = new HashMap<>();
                    hashMap.put(serverMappingNames, mapping);
                    setServerMappings(hashMap);
                }

            } catch (Exception e) {
                logger.error("Cannot read config \"omms.properties\".", e);
            }
        }
        return this;
    }


    private void setServerMappings(HashMap<String, ServerMapping> serverMappings) {
        this.serverMappings = serverMappings;
    }

    public boolean isEnableWhitelist() {
        return enableWhitelist;
    }

    public boolean isEnableChatBridge() {
        return enableChatBridge;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getWhitelistName() {
        return whitelistName;
    }

    public String getChatChannel() {
        return chatChannel;
    }

    public String getHttpQueryAddr() {
        return httpQueryAddr;
    }

    public int getHttpQueryPort() {
        return httpQueryPort;
    }

    public HashMap<String, ServerMapping> getServerMappings() {
        return serverMappings;
    }

    @Override
    public String toString() {
        return "Config{" +
                "enableWhitelist=" + enableWhitelist +
                ", enableChatBridge=" + enableChatBridge +
                ", controllerName='" + controllerName + '\'' +
                ", whitelistName='" + whitelistName + '\'' +
                ", chatChannel='" + chatChannel + '\'' +
                ", httpQueryAddr='" + httpQueryAddr + '\'' +
                ", httpQueryPort=" + httpQueryPort +
                ", serverMappings=" + serverMappings +
                '}';
    }

    public static class ServerMapping {
        private String displayName;
        private String proxyName;
        private String whitelistName;

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getProxyName() {
            return proxyName;
        }

        public void setProxyName(String proxyName) {
            this.proxyName = proxyName;
        }

        public String getWhitelistName() {
            return whitelistName;
        }

        public void setWhitelistName(String whitelistName) {
            this.whitelistName = whitelistName;
        }

        @Override
        public String toString() {
            return "ServerMapping{" +
                    "displayName='" + displayName + '\'' +
                    ", proxyName='" + proxyName + '\'' +
                    ", whitelistName='" + whitelistName + '\'' +
                    '}';
        }
    }

}
