package com.angrysurfer.atomic.user.service;
// package com.angrysurfer.social.service;

// import java.io.IOException;
// import java.util.Collections;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;

// import org.springframework.stereotype.Component;

// import lombok.extern.slf4j.Slf4j;


// @Slf4j
// @Component
// public class QuoteService {



//     public QuoteService() {
//         log.info("QuoteService initialized");
//     }
    
//     public List<String> getQuotes() {
//         log.info("Getting quotes");
//         List<String> quotes = Collections.emptyList();

//         try {
//             Document doc = Jsoup.connect("https://spetsnaz.su/~tz/skinnypuppsum/").get();
//             return doc.select("p").stream().map(el -> el.text()).collect(Collectors.toList());
//         } catch (IOException e) {
//             log.error(e.getMessage(), e);
//         }

//         return quotes;
//     }

// }
