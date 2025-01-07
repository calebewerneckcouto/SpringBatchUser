package com.devsuperior.user_request_sb.reader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.devsuperior.user_request_sb.domain.ResponseUser;
import com.devsuperior.user_request_sb.dto.UserDTO;

@Configuration
public class FetchUserDataReaderConfig implements ItemReader<UserDTO> {
	
	private static Logger logger = LoggerFactory.getLogger(FetchUserDataReaderConfig.class);

	private final String BASE_URL = "http://localhost:8081";
	private RestTemplate restTemplate = new RestTemplate();

	private int page = 0;
	private List<UserDTO> users = new ArrayList<UserDTO>();
	private int userIndex = 0;

	@Value("${chunckSize}")
	private int chunckSize;

	@Value("${pageSize}")
	private int pageSize;

	@Override
	public UserDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		UserDTO user;
		if (userIndex < users.size())
			user = users.get(userIndex);

		else
			user = null;
		userIndex++;
		return user;
	}

	private List<UserDTO> fetchUserDataFromAPI() {

		String uri = BASE_URL + "/clients/pagedData?page=%d&size=%d"; // %d ira passar os parametros...
		
		logger.info("[Reader Step] Fetching Data...");
		logger.info("[Reader Step] Request uri " + String.format(uri,getPage(),pageSize));

		ResponseEntity<ResponseUser> response = restTemplate.exchange(String.format(uri, getPage(),pageSize), HttpMethod.GET,
				null, new ParameterizedTypeReference<ResponseUser>() {
				});

		List<UserDTO> result = response.getBody().getContent();
		return result;
	}

	public int getPage() {
		return page;
	}
	
	public void incrementPage() {
		this.page++;
	}

	@BeforeChunk
	public void beforeChunck(ChunkContext context) {
		for (int i = 0; i < chunckSize; i += pageSize) {
             users.addAll(fetchUserDataFromAPI()); // carregar a lista da api
		}
	}
	
	@AfterChunk
	public void afterChunk(ChunkContext context) {
		
		logger.info("Final Chunk");
		incrementPage();
		userIndex = 0;
		users = new ArrayList<UserDTO>();
	}

}
