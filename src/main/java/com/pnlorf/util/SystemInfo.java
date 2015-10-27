package com.pnlorf.util;

import com.pnlorf.entity.ServerInfoFormMap;
import org.hyperic.sigar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by 冰诺莫语 on 2015/10/26.
 */
public class SystemInfo {

    private static final Logger logger = LoggerFactory.getLogger(SystemInfo.class);

    public static ServerInfoFormMap SystemProperty() {
        ServerInfoFormMap monitorMap = new ServerInfoFormMap();
        Runtime runtime = Runtime.getRuntime();
        Properties props = System.getProperties();
        InetAddress address = null;
        String ip = "";
        String hostName = "";
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            ip = "无法获取主机IP";
            hostName = "无法获取主机名";
            logger.error("获取InetAddress异常", e);
        }
        if (null != address) {
            try {
                ip = address.getHostAddress();
            } catch (Exception e) {
                ip = "无法获取主机IP";
                logger.error(ip, e);
            }

            try {
                hostName = address.getHostName();
            } catch (Exception e) {
                hostName = "无法获取主机名";
                logger.error(hostName, e);
            }
        }

        monitorMap.put("hostIp", ip); //本地ip地址
        monitorMap.put("hostName", hostName); // 本地主机名
        monitorMap.put("osName", props.getProperty("os.name"));//操作系统的名称
        monitorMap.put("arch", props.getProperty("os.arch")); // 操作系统的架构
        monitorMap.put("osVersion", props.getProperty("os.version"));// 操作系统的版本
        monitorMap.put("processors", runtime.availableProcessors());//JVM可以使用的处理器个数
        monitorMap.put("javaVersion", props.getProperty("java.version"));//java的运行环境版本
        monitorMap.put("vendor", props.getProperty("java.vendor")); // Java的运行环境供应商
        monitorMap.put("javaUrl", props.getProperty("java.vendor.url")); // Java供应商的URL
        monitorMap.put("javaHome", props.getProperty("java.home"));// Java的安装路径
        monitorMap.put("tmpdir", props.getProperty("java.io.tmpdir")); // 默认的临时文件路径
        return monitorMap;
    }

    public static ServerInfoFormMap memory(Sigar sigar) {
        ServerInfoFormMap monitorMap = new ServerInfoFormMap();
        try {
            Runtime runtime = Runtime.getRuntime();
            monitorMap.put("jvmTotal", Common.div(runtime.totalMemory(), (1024 * 1024), 2) + "M"); // java总内存
            monitorMap.put("jvmUse", Common.div(runtime.totalMemory() - runtime.freeMemory(), (1024 * 1024), 2) + "M"); // JVM使用内存
            monitorMap.put("jvmFree", Common.div(runtime.freeMemory(), (1024 * 1024), 2) + "M");// JVM剩余内存
            monitorMap.put("jvmUsage", Common.div(runtime.totalMemory() - runtime.freeMemory(), runtime.totalMemory(), 2));// JVM使用率

            Mem men = sigar.getMem();
            // 内存总量
            monitorMap.put("ramTotal", Common.div(men.getTotal(), (1024 * 1024 * 1024), 2) + "G"); // 内存总量
            monitorMap.put("ranUse", Common.div(men.getUsed(), (1024 * 1024 * 1024), 2) + "G"); // 当前内存使用量
            monitorMap.put("ramFree", Common.div(men.getFree(), (1024 * 1024 * 1024), 2) + "G");// 当前内存剩余量
            monitorMap.put("ranUsage", Common.div(men.getUsed(), men.getTotal(), 2)); //内存使用率

            Swap swap = sigar.getSwap();
            // 交换区总量
            monitorMap.put("swapTotal", Common.div(swap.getTotal(), (1024 * 1024 * 1024), 2) + "G");
            // 当前交换区使用量
            monitorMap.put("swapUse", Common.div(swap.getUsed(), (1024 * 1024 * 1024), 2) + "G");
            // 当前交换区剩余量
            monitorMap.put("swapFree", Common.div(swap.getFree(), (1024 * 1024 * 1024), 2) + "G");
            // 交换区使用率
            monitorMap.put("swapUsage", Common.div(swap.getUsed(), swap.getTotal(), 2));
        } catch (Exception e) {
            logger.error("获取memory信息异常", e);
        }
        return monitorMap;
    }

    public static ServerInfoFormMap usage(Sigar sigar) {
        ServerInfoFormMap monitorMap = new ServerInfoFormMap();
        try {
            Runtime runtime = Runtime.getRuntime();
            monitorMap.put("jvmUsage", Math.round(Common.div(runtime.totalMemory() - runtime.freeMemory(), runtime.totalMemory(), 2) * 100));// JVM使用率

            Mem mem = sigar.getMem();
            // 内存总量
            monitorMap.put("ramUsage", Math.round(Common.div(mem.getUsed(), mem.getTotal(), 2) * 100));// 内存使用率

            List<ServerInfoFormMap> cpu = cpuInfos(sigar);
            double b = 0.0;
            for (ServerInfoFormMap m : cpu) {
                b += Double.valueOf(m.get("cpuTotal") + "");
            }
            monitorMap.put("cpuUsage", Math.round(b / cpu.size()));// cpu使用率
        } catch (Exception e) {
            logger.error("usage 获取异常", e);
        }
        return monitorMap;
    }

    public static List<ServerInfoFormMap> cpuInfos(Sigar sigar) {
        List<ServerInfoFormMap> monitorMaps = new ArrayList<ServerInfoFormMap>();
        try {
            CpuPerc[] cpuList = sigar.getCpuPercList();
            for (CpuPerc cpuPerc : cpuList) {
                ServerInfoFormMap monitorMap = new ServerInfoFormMap();
                monitorMap.put("cpuUserUse", Math.round(cpuPerc.getUser() * 100));// 用户使用率
                monitorMap.put("cpuSysUse", Math.round(cpuPerc.getSys() * 100)); // 系统使用率
                monitorMap.put("cpuWait", Math.round(cpuPerc.getWait()) * 100);// 当前等待率
                monitorMap.put("cpuFree", Math.round(cpuPerc.getIdle()) * 100); // 当前空闲率
                monitorMap.put("cpuTotal", Math.round(cpuPerc.getCombined() * 100));//总的使用率
                monitorMaps.add(monitorMap);
            }
        } catch (Exception e) {
            logger.error("CPU Info Exception", e);
        }
        return monitorMaps;
    }

    public List<ServerInfoFormMap> diskInfos(Sigar sigar) throws Exception {
        List<ServerInfoFormMap> monitorMaps = new ArrayList<ServerInfoFormMap>();
        FileSystem[] fileSystems = sigar.getFileSystemList();
        for (int i = 0; i < fileSystems.length; i++) {
            ServerInfoFormMap monitorMap = new ServerInfoFormMap();
            FileSystem fileSystem = fileSystems[i];
            // 文件系统类型名，比如本地磁盘，光驱，网络文件系统等
            FileSystemUsage usage = null;
            usage = sigar.getFileSystemUsage(fileSystem.getDirName());
            switch (fileSystem.getType()) {
                case 0:// TYPE_UNKNOWN:位置
                    break;
                case 1: // TYPE_NONE
                    break;
                case 2: //TYPE_LOCAL_DISK 本地硬盘
                    monitorMap.put("diskName", fileSystem.getDevName()); //系统盘名称
                    monitorMap.put("diskType", fileSystem.getSysTypeName()); //盘类型
                    // 文件系统总大小
                    monitorMap.put("diskTotal", usage.getTotal());
                    // 文件系统剩余大小
                    monitorMap.put("diskFree", usage.getFree());
                    // 文件系统已经使用量
                    monitorMap.put("diskUse", usage.getUsed());
                    double usePercent = usage.getUsePercent() * 100D;
                    // 文件系统资源的利用率
                    monitorMap.put("diskUsage", usePercent); //内存使用率
                    monitorMaps.add(monitorMap);
                    break;
                case 3: // TYPE_NETWORK : 网络
                    break;
                case 4: // TYPE_RAM_DISK : 闪存
                    break;
                case 5: // TYPE_CDROM : 光驱
                    break;
                case 6: // TYPE_SWAP : 页面交换
                    break;
            }
        }
        return monitorMaps;
    }
}
