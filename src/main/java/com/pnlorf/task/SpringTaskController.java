package com.pnlorf.task;

import com.pnlorf.entity.ServerInfoFormMap;
import com.pnlorf.mapper.ServerInfoMapper;
import com.pnlorf.util.Common;
import com.pnlorf.util.EmailUtils;
import com.pnlorf.util.PropertiesUtils;
import com.pnlorf.util.SystemInfo;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Spring调度，指定时间执行
 * 利用了spring中使用注解的方式来进行任务调度
 * <p>
 * Created by 冰诺莫语 on 2015/10/27.
 */
@Component
@Lazy(false)
public class SpringTaskController {

    private final Logger logger = LoggerFactory.getLogger(SpringTaskController.class);

    @Autowired
    private ServerInfoMapper serverInfoMapper;

    /**
     * 与用户设置的使用率比较spring调度
     *
     * @throws Exception
     */
    @Scheduled(cron = "1 * * * * ? ")
    public void task() throws Exception {
        ServerInfoFormMap usage = SystemInfo.usage(new Sigar());
        String cpuUsage = usage.get("cpuUsage").toString(); // CPU使用率
        String serverUsage = usage.get("ramUsage").toString(); // 系统内存使用率
        String jvmUsage = usage.get("jvmUsage").toString();// 计算JVM内存使用率
        Properties props = PropertiesUtils.getProperties();
        String cpu = props.getProperty("cpu");
        String jvm = props.getProperty("jvm");
        String ram = props.getProperty("ram");
        String email = props.getProperty("toEmail");
        // 当系统小号内存大于或等于用户设定的内存时，发送邮件
        String cpuBool = "";
        String jvmBool = "";
        String ramBool = "";
        String mark = "<font color='red'>";
        if (Double.parseDouble(cpuUsage) > Double.parseDouble(cpu)) {
            cpuBool = "style=\"color:red; font-weight=600;\"";
            mark += "CPU当前：" + cpuUsage + "%,超出预设值 " + cpu + "%<br>";
        }

        if (Double.parseDouble(jvmUsage) > Double.parseDouble(jvm)) {
            jvmBool = "style=\"color:red;font-weight:600;\"";
            mark += "JVM当前：" + jvmUsage + "%,超出预设值 " + jvm + "%<br>";
        }

        if (Double.parseDouble(serverUsage) > Double.parseDouble(ram)) {
            ramBool = "style=\"color:red;font-weight:600;\"";
            mark += "内存当前：" + serverUsage + "%,超出预设值 " + ram + "%";
        }

        mark += "</font>";
        //邮件内容
        String title = "服务器预警提示 - " + Common.fromDateH();
        String content = "当前时间是：" + Common.fromDateH() + "<br/><br/>" + "<style type=\"text/css\">" + ".common-table{" + "-moz-user-select: none;" + "width:100%;" + "border:0;" + "table-layout : fixed;" + "border-top:1px solid #dedfe1;" + "border-right:1px solid #dedfe1;" + "}" +
                "/*header*/" + ".common-table thead td,.common-table thead th{" + "    height:23px;" + "   background-color:#e4e8ea;" + "   text-align:center;" + "   border-left:1px solid #dedfe1;" + "}" +
                ".common-table thead th, .common-table tbody th{" + "padding-left:7px;" + "padding-right:7px;" + "width:15px;" + "text-align:center;" + "}" +
                ".common-table tbody td,  .common-table tbody th{" + "    height:25px!important;" + "border-bottom:1px solid #dedfe1;" + "border-left:1px solid #dedfe1;" + "cursor:default;" + "word-break: break-all;" + "-moz-outline-style: none;" + "_padding-right:7px;" + "text-align:center;" + "}</style>"
                + "<table class=\"common-table\">" + "<thead>" + "<tr>" + "<td width=\"100\">名称</td>" + "<td width=\"100\">参数</td>" + "<td width=\"275\">预警设置</td>" + "</tr>" + "</thead>" + "<tbody id=\"tbody\">" + "<tr " + cpuBool + "><td>CPU</td><td style=\"text-align: left;\">当前使用率：" + cpuUsage
                + "%</td><td>使用率超出  " + cpu + "%,,发送邮箱提示 </td></tr>" + "<tr " + ramBool + "><td>服务器内存</td><td style=\"text-align: left;\">当前使用率：" + serverUsage + "%</td><td>使用率超出  " + ram + "%,发送邮箱提示 </td></tr>" + "<tr " + jvmBool + "><td>JVM内存</td><td style=\"text-align: left;\">当前使用率：" + jvmUsage
                + "%</td><td>使用率超出  " + jvm + "%,,发送邮箱提示 </td></tr>" + "</tbody>" + "</table>";
        mark = mark.replaceAll("'", "\"");
        if (!Common.isEmpty(cpuBool) || !Common.isEmpty(jvmBool) || !Common.isEmpty(ramBool)) {
            try {
                EmailUtils.sendMail(props.getProperty("fromEmail"), email, props.getProperty("emailName"), props.getProperty("emailPassword"), title, content);
                // 保存预警信息
                usage.put("setCpuUsage", cpu);
                usage.put("setJvmUsage", jvm);
                usage.put("setRamUsage", ram);
                usage.put("email", email);
                usage.put("mark", mark);
                serverInfoMapper.addEntity(usage);
                System.err.println("发送邮件！");
            } catch (Exception e) {
                logger.error("发送邮件失败！", e);
            }
        }
    }
}
