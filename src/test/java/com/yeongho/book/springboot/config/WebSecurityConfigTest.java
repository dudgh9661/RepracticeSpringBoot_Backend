package com.yeongho.book.springboot.config;

import com.yeongho.book.springboot.config.WebSecurityConfig;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebSecurityConfigTest {

    WebSecurityConfig webSecurityConfig = new WebSecurityConfig();
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(WebSecurityConfig.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("애플리케이션 빈 출력하기")
    public void findApplicationBean() {
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);

            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }
        }
    }

    @Test
    @DisplayName("패스워드 암호화 테스트")
    public void encodedPasswordTest() {
        //given
        String originPassword = "12345";

        //when
        String encodedPassword = passwordEncoder.encode(originPassword);

        //then
        Assertions.assertThat(originPassword).isNotEqualTo(encodedPassword);
        Assertions.assertThat(passwordEncoder.matches(originPassword, encodedPassword)).isEqualTo(true);
    }
}
