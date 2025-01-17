package ca.cihi.cims.bll.hg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetValuedMap<K, E> {
	private Map<K, Set<E>> map = new HashMap<K, Set<E>>();

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	public Set<E> get(K key) {
		return map.get(key);
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public void put(K key, E value) {
		if (!map.containsKey(key)) {
			map.put(key, new HashSet<E>());
		}
		map.get(key).add(value);
	}
}