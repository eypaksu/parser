package ef;

import com.ef.Parser;
import org.junit.Assert;
import org.junit.Test;

public class ParserTest {

    private static final String START_DATE = "2017-01-01 00:00:00";
    private static final String DURATION="hourly";
    private static final int THRESHOLD= 100;

    @Test
    public void buildQueryTest(){

        Parser parser = new Parser();
        String query = parser.buildQuery(START_DATE,DURATION,THRESHOLD);

        Assert.assertNotNull(query);

    }

}
