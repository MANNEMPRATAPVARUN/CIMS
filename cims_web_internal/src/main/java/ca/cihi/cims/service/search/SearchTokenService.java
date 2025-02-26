package ca.cihi.cims.service.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.cihi.cims.model.search.Search;

@Service
public class SearchTokenService {
	private Map<String, Search> tokens = Collections.synchronizedMap(new HashMap<String, Search>());

	public boolean check(String token) {
		return tokens.get(token) != null;
	}

	public synchronized String generate(Search search) {
		String uid = UUID.randomUUID().toString();
		tokens.put(uid, search);
		return uid;
	}

	public void remove(String token) {
		tokens.remove(token);
	}
	
	public Search get(String token){
		return tokens.get(token);
	}
}