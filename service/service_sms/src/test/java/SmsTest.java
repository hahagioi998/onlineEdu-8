import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.rmi.ServerException;

@SpringBootTest
public class SmsTest {

    @Test
    public static void main(String[] args) {
        DefaultProfile profile = DefaultProfile.getProfile(
                "cn-hangzhou",
                "LTAI4GDNby7tMtVWEwfqem6T",
                "X4O8nc7ohoHemdv1vyGdswsGV5ysjf");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = new CommonRequest();

        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");

        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", "18336597650");
        request.putQueryParameter("SignName", "小陈温馨提醒");
        request.putQueryParameter("TemplateCode", "SMS_198671346");
        request.putQueryParameter("TemplateParam", "{\"code\":\"1234567890\"}");
        try {
            //发送短信
            CommonResponse response = client.getCommonResponse(request);
            //得到json字符串格式的响应结果
            String data = response.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
