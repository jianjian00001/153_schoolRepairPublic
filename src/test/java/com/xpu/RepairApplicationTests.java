package com.xpu;

import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xpu.repair.RepairApplication;
import com.xpu.repair.enums.RepairStatusEnum;
import com.xpu.repair.mapper.MaintenanceMapper;
import com.xpu.repair.mapper.RepairMapper;
import com.xpu.repair.mapper.TechnicianMapper;
import com.xpu.repair.mapper.UrgentrepairMapper;
import com.xpu.repair.pojo.vo.UrgentrepairVo;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = RepairApplication.class)
@RunWith(SpringRunner.class)
class RepairApplicationTests {


    @Value("${server.port}")
    public String serverPort;


    @Autowired
    TechnicianMapper technicianMapper;

    @Autowired
    MaintenanceMapper maintenanceMapper;

    @Autowired
    RepairMapper repairMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    UrgentrepairMapper urgentrepairMapper;
    @Test
    void contextLoads() {
        Page<UrgentrepairVo> page = new Page<>(1,1);
        List<UrgentrepairVo> urgentrepairVoList = urgentrepairMapper.listVo(page,null,null);
        for (UrgentrepairVo urgentrepairVo : urgentrepairVoList) {
            urgentrepairVo.setStatus(RepairStatusEnum.getById(urgentrepairVo.getRepair().getStatus()).getStatusName());
        }
        page.setRecords(urgentrepairVoList);
        System.out.println(page);
    }

    @Value("${aliyun.oss.file.endpoint}")
    String endpoint;

    @Value("${aliyun.oss.file.keyid}")
    String accessKeyId;

    @Value("${aliyun.oss.file.keysecret}")
    String accessKeySecret;

    @Value("${aliyun.oss.file.bucketname}")
    String bucketName;

    @Test
    void testCreateBucket() {
        //创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint,accessKeyId,accessKeySecret);
        //创建存储空间
//        ossClient.createBucket("mxh-test-file");
        System.out.println(serverPort);
        //查看指定存储空间是否存在
        boolean bucketExist = ossClient.doesBucketExist(bucketName);
        System.out.println(bucketExist);
        //关闭OSSClient
        ossClient.shutdown();
    }


}
