package lithium.service.datafeed.provider.google.service;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({PubSubExchangeStreamConfiguration.class})
public @interface EnablePubSubExchangeStream {
}