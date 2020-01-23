package uk.gov.ch.developer.docs.controller.developer;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.gov.ch.developer.docs.controller.AbstractPageController;
import uk.gov.ch.developer.docs.controller.BaseController;
import uk.gov.ch.developer.docs.session.SessionService;
import uk.gov.companieshouse.session.Session;
import uk.gov.companieshouse.session.SessionKeys;
import uk.gov.companieshouse.session.handler.SessionHandler;

@Controller
@RequestMapping("${callback.url}")
public class UserCallbackController extends BaseController{
    
//    static String title = "Hello";
//    
//    String path = "dev-hub/gettingStarted";
//    
//    public UserCallbackController() {
//        super(title);
//    }
//
//    @Override
//    public String getPath() {
//        return path;
//    }
    @Autowired
    SessionService sessionService;
    
    @GetMapping
    @ResponseBody
    public String getParams(@RequestParam Optional<String> state) {
        LOGGER.info("State: " + state.orElseGet(() -> "Not available!!!"));
        
        Optional<Map<String,Object>> session = Optional.ofNullable(sessionService.getSessionDataFromContext());
        
        session.ifPresent(d -> System.out.print("Session is not null!!!!!"));
        
//        JWEObject jweObject = JWEObject.parse(jweString);
//
//        byte[] key = Base64.decodeBase64(base64Key);
//        jweObject.decrypt(new DirectDecrypter(key));
//
//        Payload payload = jweObject.getPayload();
//
//        console.println("JWE payload:" + payload.toString());
        
        return "Found";
    }

    @Override
    protected String getTemplateName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTemplateTitle() {
        // TODO Auto-generated method stub
        return null;
    }

}
