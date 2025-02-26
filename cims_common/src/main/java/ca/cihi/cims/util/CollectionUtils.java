package ca.cihi.cims.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils {

	public static <E> Set<E> asSet(E... elements) {
		return new HashSet<E>(Arrays.asList(elements));
	}

}
