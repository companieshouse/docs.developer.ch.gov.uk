package uk.gov.ch.developer.docs.models.nav;

import java.util.List;

public interface INavBarItem {

    String getHeading();

    String getUrl();

    NavBarItem getParent();

    List<INavBarItem> getChildren();

    int getDepth();
}
