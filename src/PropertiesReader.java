import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class PropertiesReader {

	private static final String FILE_NAME = "system.properties";

	private static final String SERVER_ADDRESS = "RW.server";
	private static final String SERVER_PORT = "RW.server.port";
	private static final String NUMBER_OF_ACCESSES = "RW.numberOfAccesses";

	private static final String NUMBER_OF_READERS = "RW.numberOfReaders";
	private static final String NUMBER_OF_WRITERS = "RW.numberOfWriters";

	private static final String READER_PREFIX = "RW.reader";
	private static final String WRITER_PREFIX = "RW.writer";

	private final Map<String, String> properties;
	private int nextReader;
	private int nextWriter;

	public PropertiesReader() throws FileNotFoundException {
		Scanner reader = new Scanner(new File(FILE_NAME));
		properties = new HashMap<String, String>();
		while (reader.hasNext()) {
			String line[] = reader.nextLine().trim().split("=");
			properties.put(line[0].trim(), line[1].trim());
		}
		reader.close();
		this.nextReader = 0;
		this.nextWriter = 0;
	}

	public String getServerAddress() {
		return properties.get(SERVER_ADDRESS);
	}

	public int getServerPortNum() {
		return Integer.valueOf(properties.get(SERVER_PORT));
	}

	public int getAccessNum() {
		return Integer.valueOf(properties.get(NUMBER_OF_ACCESSES));
	}

	public int getReadersNum() {
		return Integer.valueOf(properties.get(NUMBER_OF_READERS));
	}

	public int getWritersNum() {
		return Integer.valueOf(properties.get(NUMBER_OF_WRITERS));
	}

	public String getNextReader() {
		return properties.get(READER_PREFIX + (nextReader++));
	}

	public String getNextWriter() {
		return properties.get(WRITER_PREFIX + (nextWriter++));
	}
}
