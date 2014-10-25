package com.d_project.ui;

import java.awt.Dimension;

/**
 * DLayoutManager
 * @author Kazuhiko Arase
 */
public interface DLayoutManager {
    void layoutContainer(DContainer cont);
    Dimension preferredLayoutSize(DContainer cont);
    void addLayoutComponent(String name, DComponent comp);
    void removeLayoutComponent(DComponent comp);
}
