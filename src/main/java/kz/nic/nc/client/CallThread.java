package kz.nic.nc.client;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import kz.nic.nc.core.CallMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CallThread implements Runnable {

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Autowired
    private TaskExecutor taskExecutor;

    private JFrame wnd;
    private JTextArea textArea;

    @Value("${user.phonenum}")
    private String userPhoneNum;

    @Value("${rest.api.user}")
    private String restApiUser;
    @Value("${rest.api.password}")
    private String restApiPassword;

    @PostConstruct
    public void startThread() {

        wnd = new JFrame();
        wnd.setTitle("Уведомление");
        wnd.setBounds(100, 100, 300, 300);
        wnd.setAlwaysOnTop(true);
        textArea = new JTextArea();
        wnd.add(textArea);

        taskExecutor.execute(this);
    }

    @Override
    public void run() {
        final String uri = "http://localhost:8080/api/callmsg/findUnDoneLast/";
        final String uri1 = "http://localhost:8080/api/callmsg/setDone/";

        RestTemplate restTemplate = restTemplateBuilder.basicAuthentication(restApiUser, restApiPassword).build();

        try {
            while (true) {

                try {
                    ResponseEntity<List<CallMsg>> rateResponse
                            = restTemplate.exchange(uri + userPhoneNum,
                                    HttpMethod.GET, null, new ParameterizedTypeReference<List<CallMsg>>() {
                            });
                    List<CallMsg> msgs = rateResponse.getBody();

                    if (!msgs.isEmpty()) {

                        for (CallMsg msg : msgs) {

                            restTemplate.getForObject(uri1 + msg.getId(), CallMsg.class);

                            if (msg.getType().equals("call_ring")) {
                                textArea.setText("Звонит - " + msg.getCallerIdName());
                                wnd.setVisible(true);
                            }

                        }
                    }
                } catch (RestClientException ex) {
                }

                Thread.sleep(100);

            }
        } catch (InterruptedException ex) {
        }
    }

}
