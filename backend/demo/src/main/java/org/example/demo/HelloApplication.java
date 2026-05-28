package org.example.demo;

import org.example.demo.repositories.*;

import org.example.demo.services.UserService;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.ApplicationPath;
import javax.inject.Singleton;

@ApplicationPath("/api")
public class HelloApplication extends ResourceConfig {
    public HelloApplication() {
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);


        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                this.bind(MySqlUserRepository.class)
                        .to(UserRepository.class)
                        .in(Singleton.class);




                this.bindAsContract(UserService.class).in(Singleton.class);

            }
        };
        register(binder);

        packages("org.example.demo");
    }
}