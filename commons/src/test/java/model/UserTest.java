package model;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;


/**
 * Created by Wiktor on 20.03.2017.
 */
public class UserTest {
    @Test
    public void exampleTest() throws Exception {
        User user = new User();
        user.setFirstName("Andrzej");
        assertThat(user.getFirstName()).isEqualTo("Andrzej");
    }

    @Before
    public void setUp() throws Exception {

    }


}