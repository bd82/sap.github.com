package sap.github;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DataRetrieval {

	private static final String urlBase = "https://api.github.com/orgs/sap";
	private static final String url = "https://api.github.com/orgs/sap/repos";

	private static final ObjectMapper mapper = new ObjectMapper(); // just need one

	private static final String[] keysOfInterest = { "id", "name", "private", "html_url", "description", "fork", "created_at", "updated_at", "language" };

	@SuppressWarnings("unchecked")
	public static void main( String[] args ) {
		try {
			System.out.println( "Total repos: " + getRepoCount() );

			URL jsonUrl2 = new URL( url );
			URLConnection urlConnection = jsonUrl2.openConnection();

			JsonNode node = mapper.readTree( urlConnection.getInputStream() );

			// System.out.println( "Received json node: " + node );

			Map<String, Object>[] result = mapper.convertValue( node, Map[].class );

			Joiner.MapJoiner mapJoiner = Joiner.on( "\n\t" )
											   .withKeyValueSeparator( "=" )
											   .useForNull( "null" );

			List<Map<String, Object>> outputData = Lists.newLinkedList();

			int count = 0;
			for (Map<String, Object> item : result) {
				// System.out.println( "Item " + count++ + " - Retrieved the following:\n\t" + mapJoiner.join( item ) );

				Map<String, Object> filteredItems = Maps.filterKeys( item, Predicates.in( Lists.newArrayList( keysOfInterest ) ) );
				outputData.add( filteredItems );
			}

			count = 0;
			for (Map<String, Object> item : outputData) {
				System.out.println( "Item " + count++ + " - Retrieved the following:\n\t" + mapJoiner.join( item ) );
			}
			
			

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getRepoCount() throws MalformedURLException, IOException {
		URLConnection conn = new URL( urlBase ).openConnection();
		JsonNode node = mapper.readTree( conn.getInputStream() );

		return node.get( "public_repos" )
				   .asInt();
	}
}
