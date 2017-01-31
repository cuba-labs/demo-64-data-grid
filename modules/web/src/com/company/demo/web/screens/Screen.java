package com.company.demo.web.screens;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.security.entity.User;

import javax.inject.Inject;
import java.util.Map;

public class Screen extends AbstractWindow {
    @Inject
    private DataGrid<User> dataGrid;

    @Override
    public void init(Map<String, Object> params) {
    }
}