package uk.gov.ch.developer.docs.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NavBarModel {

    private Map<String, List<INavBarItem>> sections;

    public NavBarModel() {
        this.sections = new LinkedHashMap<>();
        ArrayList<INavBarItem> items = new ArrayList<>();
        NavBarItem view_all_applications = new NavBarItem("View all applications", "example.com");
        view_all_applications.add("Default Application", "www.example.com");
        items.add(view_all_applications);
        items.add(new NavBarItem("Add an application", "example.com"));
        this.sections.put("Manage applications", items);
        this.sections.put("Documentation", new ArrayList<>());
        this.sections.put("Manage account", new ArrayList<>());
        this.sections.put("Help", new ArrayList<>());
    }

    public Map<String, List<INavBarItem>> getSections() {
        return sections;
    }

}
