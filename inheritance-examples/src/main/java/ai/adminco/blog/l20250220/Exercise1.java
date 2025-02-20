package ai.adminco.blog.l20250220;

import java.util.Map;

public class Exercise1 {

   public static class BaseRestClient {
	  @SuppressWarnings("unused")
	  private final String url;

	  public BaseRestClient(String url) {
		 this.url = url;
	  }

	  public byte[] get(String relativePath, Map<String, String> query) {
		 throw new RuntimeException( "Not implemented yet" ); // TODO
	  }
   }

   public static class Client1 extends BaseRestClient {

	  public Client1(String url) {
		 super( url );
	  }

   }

   public static class Client2 extends BaseRestClient {

	  public Client2(String url) {
		 super( url );
	  }

   }
}
