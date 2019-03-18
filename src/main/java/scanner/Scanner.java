package scanner;

import connection.soup.Helper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Scanner {

    //private final Set<String> listOfLinks;
    private final LinkedHashSet<String> listOfLinks;

    private final int MAX_DEPTH = 2;

    public Scanner() {
        listOfLinks = new LinkedHashSet();
    }

    public void scanDomain(String site, int depth) {
        try {
            System.out.println("D: "+depth+", Page: "+site);

            Document page = Helper.getDocument(site);
            scanPage(page);

            int change_counter_1 = listOfLinks.size();
            for(Element link : page.select("a")) {
                listOfLinks.add(link.attr("abs:href"));
                int change_counter_2 = listOfLinks.size();
                if(change_counter_1 == change_counter_2) {
                    if(depth <= MAX_DEPTH) scanDomain(link.attr("abs:href"), depth+1);
                    else return;
                } else change_counter_1 = change_counter_2;
            }

            //for(String link_text : temp_list) listOfLinks.add(link_text);
        } catch(IOException | IllegalArgumentException exc) {
            System.out.println(exc.toString());
        }
    }

    public void scanPage(Document doc) {
        Elements elements = doc.select("*");

        for(Element element : elements) {
            String extracted_mail = Helper.extractFirstEmail(element.text());
            if(!extracted_mail.equals("")) System.out.println(extracted_mail);
        }
    }

    public LinkedHashSet<String> getList() {
        return this.listOfLinks;
    }

    public void buffer() {
        Iterator<String> iterator = listOfLinks.iterator();
        while(iterator.hasNext()) {
            System.out.println("Buffered: "+iterator.next());
        }
    }
}
