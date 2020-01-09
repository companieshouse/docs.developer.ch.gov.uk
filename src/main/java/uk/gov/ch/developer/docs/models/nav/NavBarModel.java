package uk.gov.ch.developer.docs.models.nav;

import java.util.LinkedHashMap;
import java.util.Map;

public class NavBarModel {

    private Map<String, NavItemList> sections = new LinkedHashMap<>();

    public Map<String, NavItemList> getSections() {
        return sections;
    }

    public NavItemList addHeading(String heading) {
        return sections.computeIfAbsent(
                heading,
                k -> new NavItemList()
        );
    }
}
