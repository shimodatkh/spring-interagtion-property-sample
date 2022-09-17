package com.example.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import com.rometools.rome.feed.synd.SyndEntryImpl;

@SpringBootTest({ "auto.startup=false", // we don't want to start the real feed
		"feed.file.name=Test" }) // use a different file
		@Configuration
		@PropertySource(value = "classpath:/hoge/fuga.properties")
public class IntegrationApplicationTests {

	@Autowired
	private SourcePollingChannelAdapter newsAdapter;

	@Autowired
	private MessageChannel news;

	@Value("${aaa.list1}")
	private Set<String> hoge;
	@Value("${aaa.bbb}")
	private String hoge2;
	@Test
	public void test() throws Exception {
		System.out.println(hoge);
		for (String string : hoge) {
			System.out.println(string);
		}
		System.out.println(hoge2);
		assertThat(this.newsAdapter.isRunning()).isFalse();
		SyndEntryImpl syndEntry = new SyndEntryImpl();
		syndEntry.setTitle("Test Title");
		syndEntry.setLink("http://characters/frodo");
		File out = new File("C:\\git\\integration\\Test");
		out.delete();
		assertThat(out.exists()).isFalse();
		this.news.send(MessageBuilder.withPayload(syndEntry).build());
		assertThat(out.exists()).isTrue();
		BufferedReader br = new BufferedReader(new FileReader(out));
		String line = br.readLine();
		assertThat(line).isEqualTo("Test Title @ http://characters/frodo");
		br.close();
		// out.delete();
	}

}
