package dev.cyberjar.neurowatch.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(
            LocalValidatorFactoryBean factory) {
        return new ValidatingMongoEventListener(factory);
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    @Primary
    public MongoClient mongoClient(MongoConnectionDetails details) {
        MongoClient initialClient = MongoClients.create(details.getConnectionString());
        return new MongoClientProxy(initialClient);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient client) {
        return new MongoTemplate(client, "neurowatch");
    }

    @Component
    static public class MongoClientResource implements Resource {

        private final MongoClientProxy mongoClientProxy;
        private final MongoConnectionDetails details;

        public MongoClientResource(MongoClient mongoClientProxy, MongoConnectionDetails details) {
            this.mongoClientProxy = (MongoClientProxy) mongoClientProxy;
            this.details = details;
            Core.getGlobalContext().register(this);
        }

        @Override
        public void beforeCheckpoint(Context<? extends Resource> context) {
            mongoClientProxy.delegate.close();
        }

        @Override
        public void afterRestore(Context<? extends Resource> context) {
            mongoClientProxy.delegate = MongoClients.create(details.getConnectionString());
        }
    }
}
