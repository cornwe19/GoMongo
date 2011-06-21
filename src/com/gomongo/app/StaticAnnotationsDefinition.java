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
			
			mAnnotations.put( 0, Arrays.asList( R.drawable.hat, R.drawable.detective, R.drawable.hat, R.drawable.hat ) );
			mAnnotations.put( 1, Arrays.asList( R.drawable.hat, R.drawable.detective, R.drawable.detective, R.drawable.hat ) );
			mAnnotations.put( 2, Arrays.asList( R.drawable.detective, R.drawable.detective, R.drawable.hat, R.drawable.detective ) );
		}
		
		return mAnnotations;
	}
}
