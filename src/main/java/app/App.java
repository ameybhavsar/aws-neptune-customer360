package app;

import app.neptune.NeptuneExtension;
import app.neptune.NeptuneQueries;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.NamedCsvReader;
import de.siegmar.fastcsv.writer.CsvWriter;
import de.siegmar.fastcsv.writer.QuoteStrategy;
import io.jooby.*;
import io.jooby.json.JacksonModule;
import io.jooby.rocker.RockerModule;
import io.jooby.whoops.WhoopsModule;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class App extends Jooby {

  static Map<String, Object> getNodeID(String value) {
    return NeptuneQueries.nodeByValue(value);
  }

  static List<Map<String, Object>> getRels(String id) {
    return NeptuneQueries.relatedById(id);
  }
  
   static List<Map<String, Object>> getBankCustomers(String id) {
    return NeptuneQueries.getCustomerOfBank(id);
  }
  
   static List<Map<String, Object>> getBankTeller(String id) {
    return NeptuneQueries.getTellerOfBank(id);
  }

  public static LoadingCache<String, Map<String, Object>> NODE_ID_CACHE = Caffeine.newBuilder()
          .maximumSize(100_000)
          .expireAfterWrite(30, TimeUnit.DAYS)
          .build(App::getNodeID);

  public static LoadingCache<String, List<Map<String, Object>>> REL_CACHE = Caffeine.newBuilder()
          .maximumSize(100_000)
          .expireAfterWrite(30, TimeUnit.DAYS)
          .build(App::getRels);
          
  public static LoadingCache<String, List<Map<String, Object>>> BANK_CUSTOMER_CACHE = Caffeine.newBuilder()
          .maximumSize(100_000)
          .expireAfterWrite(30, TimeUnit.DAYS)
          .build(App::getBankCustomers);
          
  public static LoadingCache<String, List<Map<String, Object>>> BANK_TELLER_CACHE = Caffeine.newBuilder()
          .maximumSize(100_000)
          .expireAfterWrite(30, TimeUnit.DAYS)
          .build(App::getBankTeller);        

  public static HashMap<String, Object> FINDING = new HashMap<>();
  public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
  public static ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

  {
    // Debug friendly error messages
    install(new WhoopsModule());

    setServerOptions(new ServerOptions().setPort(80));

    // Template
    install(new RockerModule());
    install(new JacksonModule());
    install(new NeptuneExtension());

    // Handle Cors
    decorator(new CorsHandler());

    // Static Assets
    Path assets = Paths.get("assets");
    AssetSource www = AssetSource.create(assets);
    assets("/assets/?*", new AssetHandler(www)
            .setMaxAge(Duration.ofDays(365)));

    assets("/robots.txt", assets.resolve("robots.txt"));
    assets("/favicon.ico", assets.resolve("favicon.ico"));

    // Handle Errors
    error((ctx, cause, statusCode) -> {
      Router router = ctx.getRouter();
      router.getLog().error("found `{}` error", statusCode.value(), cause);

      String code = String.valueOf(statusCode.value());
      code = code.replaceAll("0", "&#x1f635;");


      ctx.render(views.error.template(code, cause.getMessage()));
    });

    get("/edges/{id}", ctx -> {
      ctx.setResponseType(MediaType.json);

      String id = ctx.path("id").value();
      if (id.startsWith("name-")) {
        id = id.replaceAll(Pattern.quote("+"), " ");
      }
      // Prepare the results:
      ArrayList<HashMap<String, Object>> results = new ArrayList<>();

      REL_CACHE.get(id).forEach( entry -> {
        HashMap<String, Object> result = new HashMap<>();
        result.put("source", entry.get("id"));
        result.put("target", entry.get("id2"));

        HashMap<String, String> sd = new HashMap<>();
        List<String> labels = (List<String>)entry.get("labels");
        sd.put("type", labels.get(0));
        sd.put("label", (String)entry.get("value"));

        HashMap<String, String> td = new HashMap<>();
        List<String> labels2 = (List<String>)entry.get("labels2");
        td.put("type", labels2.get(0));
        td.put("label", (String)entry.get("value2"));

        result.put("source_data", sd);
        result.put("target_data", td);
        results.add(result);
      });


      return results;
    });
    
    get("/edges1/{id}", ctx -> {
      ctx.setResponseType(MediaType.json);

      String id = ctx.path("id").value();
      if (id.startsWith("name-")) {
        id = id.replaceAll(Pattern.quote("+"), " ");
      }
      // Prepare the results:
      ArrayList<HashMap<String, Object>> results = new ArrayList<>();

      BANK_CUSTOMER_CACHE.get(id).forEach( entry -> {
        HashMap<String, Object> result = new HashMap<>();
        result.put("source", entry.get("id"));
        result.put("target", entry.get("id2"));

        HashMap<String, String> sd = new HashMap<>();
        List<String> labels = (List<String>)entry.get("labels");
        sd.put("type", labels.get(0));
        sd.put("label", (String)entry.get("value"));

        HashMap<String, String> td = new HashMap<>();
        List<String> labels2 = (List<String>)entry.get("labels2");
        td.put("type", labels2.get(0));
        td.put("label", (String)entry.get("value2"));

        result.put("source_data", sd);
        result.put("target_data", td);
        results.add(result);
      });

      return results;
    });
    
    get("/edges2/{id}", ctx -> {
      ctx.setResponseType(MediaType.json);

      String id = ctx.path("id").value();
      if (id.startsWith("name-")) {
        id = id.replaceAll(Pattern.quote("+"), " ");
      }
      // Prepare the results:
      ArrayList<HashMap<String, Object>> results = new ArrayList<>();

      BANK_TELLER_CACHE.get(id).forEach( entry -> {
        HashMap<String, Object> result = new HashMap<>();
        result.put("source", entry.get("id"));
        result.put("target", entry.get("id2"));

        HashMap<String, String> sd = new HashMap<>();
        List<String> labels = (List<String>)entry.get("labels");
        sd.put("type", labels.get(0));
        sd.put("label", (String)entry.get("value"));

        HashMap<String, String> td = new HashMap<>();
        List<String> labels2 = (List<String>)entry.get("labels2");
        td.put("type", labels2.get(0));
        td.put("label", (String)entry.get("value2"));

        result.put("source_data", sd);
        result.put("target_data", td);
        results.add(result);
      });

      return results;
    });
    
    // Load index template file.
    get("/", ctx -> views.index.template());

    // Load customer profile template file.
    get("/profile", ctx -> views.profile.template());
    
    // Load customer recommendation template file.
    get("/recommendation", ctx -> views.recommendation.template());
    
    // Load bank agent template file.
    get("/banker", ctx -> views.banker.template());

    // Optional
    get("/match", ctx -> {
      Runnable runnableTask = () -> {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        FINDING.put("started", zonedDateTime.format(FORMATTER));

        AtomicLong queryCount = new AtomicLong();
        AtomicLong matchCount = new AtomicLong();
        AtomicLong customerCount = new AtomicLong();

        try {
          File customerFile = new File("customers.csv");
          File foundFile = new File("found.csv");
          foundFile.createNewFile();
          File ringsFile = new File("rings.csv");
          ringsFile.createNewFile();
          File skipFile = new File("skip.csv");
          skipFile.createNewFile();
          HashSet<String> skip = new HashSet<>();

          Writer foundWriter = new FileWriter(foundFile);
          CsvWriter foundCSVWriter = CsvWriter.builder().quoteStrategy(QuoteStrategy.ALWAYS).build(foundWriter);

          Writer ringsWriter = new FileWriter(ringsFile);
          CsvWriter ringsCSVWriter = CsvWriter.builder().quoteStrategy(QuoteStrategy.ALWAYS).build(ringsWriter);

          NamedCsvReader customerCSV = NamedCsvReader.builder()
                  .build(customerFile.toPath(), Charset.defaultCharset());
          CsvReader skipCSV = CsvReader.builder().
                  build(skipFile.toPath(), Charset.defaultCharset());

          skipCSV.forEach( csvRow -> skip.add(csvRow.getField(0)));
          skipCSV.close();

          Writer skipWriter = new FileWriter(skipFile, true);
          CsvWriter skipCSVWriter = CsvWriter.builder().quoteStrategy(QuoteStrategy.ALWAYS).build(skipWriter);

          foundCSVWriter.writeRow("id", "id2", "shared");
          foundWriter.flush();

          ringsCSVWriter.writeRow("id", "id2", "id3", "id4");
          ringsWriter.flush();

          customerCSV.forEach(csvRow -> {
            String customerID = csvRow.getField("id");
            FINDING.put("customers", customerCount.incrementAndGet());
            // Skip ones we've already tried
            if (skip.contains(customerID)) {
              return;
            }
            FINDING.put("queries", queryCount.incrementAndGet());

            List<Map<String, Object>> list = NeptuneQueries.connected(customerID);
            if (list.isEmpty()) {
              skipCSVWriter.writeRow(customerID);
              try {
                skipWriter.flush();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }

            list.forEach(connection -> {
              // Rule: Skip name and username only matches
              List<String> shared = (List<String>) connection.get("shared");
              if (shared.size() == 1) {
                String only = shared.get(0);
                if (only.startsWith("name-") || only.startsWith("username")) {
                  return;
                }
              }

              // Rule: Skip username-abc
              if (shared.size() == 2) {
                String first = shared.get(0);
                if (first.equals("username-abc")) {
                  return;
                }
              }

              FINDING.put("matches", matchCount.incrementAndGet());
              FINDING.put("last_match", ZonedDateTime.now().format(FORMATTER));
              foundCSVWriter.writeRow(
                      (String) connection.get("id"),
                      (String) connection.get("id2"),
                      String.join("|", shared));
            });

            List<Map<String, Object>> ring = NeptuneQueries.fraudRing(customerID);
            if (!ring.isEmpty()) {
              ring.forEach(customers -> {
                  ringsCSVWriter.writeRow(
                          (String) customers.get("id"),
                          (String) customers.get("id2"),
                          (String) customers.get("id3"),
                          (String) customers.get("id4"));
              });
            }

            try {
              foundWriter.flush();
              ringsWriter.flush();

            } catch (IOException e) {
              e.printStackTrace();
            }
          });

          foundCSVWriter.close();
          foundWriter.close();

          ringsCSVWriter.close();
          ringsWriter.close();
          FINDING.put("ended", ZonedDateTime.now().format(FORMATTER));

        } catch (Exception e) {
          e.printStackTrace();
        }

      };
      // Only run this once
      if (!FINDING.containsKey("started")) {
        EXECUTOR.execute(runnableTask);
      }
      return FINDING;
    });

    // Optional
    get("/results", ctx -> {
      ArrayList<Map<String, String>> results = new ArrayList<>();
      File foundFile = new File("found.csv");
      NamedCsvReader foundCSV =  NamedCsvReader.builder()
              .build(foundFile.toPath(), Charset.defaultCharset());
      foundCSV.forEach( csvRow -> {
        results.add(csvRow.getFields());
      });

      return views.results.template(results);
    });
    
    // Optional
    get("/rings", ctx -> {
      ArrayList<Map<String, String>> results = new ArrayList<>();
      File ringsFile = new File("rings.csv");
      NamedCsvReader ringsCSV =  NamedCsvReader.builder()
              .build(ringsFile.toPath(), Charset.defaultCharset());
      ringsCSV.forEach( csvRow -> {
        results.add(csvRow.getFields());
      });

      return views.rings.template(results);
    });
  }

  public static void main(final String[] args) {
    runApp(args, App::new);
  }

}
