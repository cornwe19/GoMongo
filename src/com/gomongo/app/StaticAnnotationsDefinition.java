package com.gomongo.app;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticAnnotationsDefinition {
	private static Map<Integer,List<Integer>> mAnnotations;
	
	public static Map<Integer,List<Integer>> getAnnotations() {
		if( mAnnotations == null ) {
			mAnnotations = new HashMap<Integer,List<Integer>>();
			
			mAnnotations.put( 0, Arrays.asList( R.drawable.mongo_logo, R.drawable.mongo_green, R.drawable.cartoon_soldier, R.drawable.heart ) );
			mAnnotations.put( 1, Arrays.asList( R.drawable.swords, R.drawable.head, R.drawable.mustache, R.drawable.hat ) );
		}
		
		return mAnnotations;
	}
}
