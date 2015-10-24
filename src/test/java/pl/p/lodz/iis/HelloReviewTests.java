package pl.p.lodz.iis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import pl.p.lodz.iis.hr.HelloReview;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = HelloReview.class)
@WebAppConfiguration
public class HelloReviewTests {

    @Test
    public void contextLoads() {
    }

}
