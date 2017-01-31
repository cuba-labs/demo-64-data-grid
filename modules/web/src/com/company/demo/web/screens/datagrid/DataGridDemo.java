package com.company.demo.web.screens.datagrid;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.DataGrid.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.User;
import com.vaadin.server.FontAwesome;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DataGridDemo extends AbstractWindow {

    @Override
    public void init(Map<String, Object> params) {
        initBasicDataGrid();
        initFrozenColumnsDataGrid();
        initSelectionModeDataGrid();
        initListenersDataGrid();
        initStyledDataGrid();
        initLargeDataSetDataGrid();
        initRendererDataGrid();
        initRendererAndConverterDataGrid();
    }

    @Inject
    private DataGrid<User> basicDataGrid;
    @Inject
    private CheckBox columnsCollapsingAllowed;
    @Inject
    private CheckBox headerVisible;
    @Inject
    private CheckBox reorderingAllowed;
    @Inject
    private CheckBox sortable;

    private void initBasicDataGrid() {
        sortable.setValue(basicDataGrid.isSortable());
        columnsCollapsingAllowed.setValue(basicDataGrid.isColumnsCollapsingAllowed());
        reorderingAllowed.setValue(basicDataGrid.isColumnReorderingAllowed());
        headerVisible.setValue(basicDataGrid.isHeaderVisible());

        columnsCollapsingAllowed.addValueChangeListener(e ->
                basicDataGrid.setColumnsCollapsingAllowed(BooleanUtils.isTrue((Boolean) e.getValue())));
        headerVisible.addValueChangeListener(e ->
                basicDataGrid.setHeaderVisible(BooleanUtils.isTrue((Boolean) e.getValue())));
        reorderingAllowed.addValueChangeListener(e ->
                basicDataGrid.setColumnReorderingAllowed(BooleanUtils.isTrue((Boolean) e.getValue())));
        sortable.addValueChangeListener(e ->
                basicDataGrid.setSortable(BooleanUtils.isTrue((Boolean) e.getValue())));
    }

    @Inject
    private DataGrid<User> frozenColumnsDataGrid;
    @Inject
    private TextField frozenColumnCountField;

    private void initFrozenColumnsDataGrid() {
        frozenColumnCountField.setValue(frozenColumnsDataGrid.getFrozenColumnCount());
        frozenColumnCountField.addValueChangeListener(e ->
                frozenColumnsDataGrid.setFrozenColumnCount(frozenColumnCountField.getValue()));
    }

    @Inject
    private DataGrid<User> selectionModeDataGrid;
    @Inject
    private LookupField selectionModeField;

    private void initSelectionModeDataGrid() {
        Map<String, SelectionMode> selectionModeMap = new LinkedHashMap<>();
        for (SelectionMode mode : SelectionMode.values()) {
            selectionModeMap.put(mode.name(), mode);
        }
        selectionModeField.setOptionsMap(selectionModeMap);
        selectionModeField.setValue(selectionModeDataGrid.getSelectionMode());

        selectionModeField.addValueChangeListener(e ->
                selectionModeDataGrid.setSelectionMode((SelectionMode) e.getValue()));
    }

    @Inject
    private DataGrid<User> listenersDataGrid;

    private void initListenersDataGrid() {
        listenersDataGrid.addSelectionListener(event ->
                showNotification("SelectionEvent", getEventInfo(event), NotificationType.HUMANIZED_HTML));

        listenersDataGrid.addSortListener(event ->
                showNotification("SortEvent", getEventInfo(event), NotificationType.HUMANIZED_HTML));

        listenersDataGrid.addColumnReorderListener(event ->
                showNotification("ColumnReorderEvent", NotificationType.HUMANIZED));

        listenersDataGrid.addColumnResizeListener(event ->
                showNotification("ColumnResizeEvent", getEventInfo(event), NotificationType.HUMANIZED_HTML));

        listenersDataGrid.addColumnCollapsingChangeListener(event ->
                showNotification("ColumnCollapsingChangeEvent", getEventInfo(event), NotificationType.HUMANIZED_HTML));
    }

    private String getEventInfo(SelectionEvent<User> event) {
        return "<strong>Added:</strong> " + event.getAdded().size() + "<br>" +
                "<strong>Removed:</strong> " + event.getRemoved().size() + "<br>" +
                "<strong>Selected:</strong> " + event.getSelected().size();
    }

    private String getEventInfo(SortEvent event) {
        return "<strong>Column ID:</strong> " + event.getSortOrder().get(0).getColumnId();
    }

    private String getEventInfo(ColumnResizeEvent event) {
        return "<strong>Column ID:</strong> " + event.getColumn();
    }

    private String getEventInfo(ColumnCollapsingChangeEvent event) {
        return "<strong>Column ID:</strong> " + event.getColumn() + "<br>" +
                "<strong>Collapsed:</strong> " + event.isCollapsed();
    }

    @Inject
    private DataGrid<User> rowStyleDataGrid;

    @Inject
    private DataGrid<User> cellStyleDataGrid;

    private void initStyledDataGrid() {
        rowStyleDataGrid.addRowStyleProvider(entity ->
                entity.getActive() ? null : "inactive");

        cellStyleDataGrid.addCellStyleProvider((entity, property) ->
                "createTs".equals(property) ? "right-align" : null);
    }

    @Inject
    private TextField usersCountField;

    @Inject
    private CollectionDatasource<User, UUID> usersDs;

    @Inject
    private Metadata metadata;

    private void initLargeDataSetDataGrid() {
        usersCountField.setValue(1000);
    }

    public void generateUsers() {
        int count = usersCountField.getValue();
        usersDs.refresh();

        for (int i = 0; i < count; i++) {
            usersDs.includeItem(createUser(i, "test"));
        }
    }

    private User createUser(int index, String name) {
        User user = metadata.create(User.class);
        user.setLogin(name + index);
        user.setPassword(name + index);
        user.setName(StringUtils.capitalize(name + index));
        return user;
    }

    @Inject
    private DataGrid<User> beforeRendererDataGrid;

    @Inject
    private DataGrid<User> rendererDataGrid;

    private void initRendererDataGrid() {
        ColumnGenerator<User, String> avatarGenerator = new ColumnGenerator<User, String>() {
            @Override
            public String getValue(ColumnGeneratorEvent<User> event) {
                return "images/user.svg";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
        };

        Column column = beforeRendererDataGrid.addGeneratedColumn("userAvatar", avatarGenerator, 0);
        column.setCaption("Avatar");

        column = rendererDataGrid.addGeneratedColumn("userAvatar", avatarGenerator, 0);
        column.setCaption("Avatar");
        column.setRenderer(rendererDataGrid.createRenderer(ImageRenderer.class));
    }

    @Inject
    private DataGrid<User> beforeRendererAndConverterDataGrid;

    @Inject
    private DataGrid<User> noRendererAndConverterDataGrid;

    @Inject
    private DataGrid<User> rendererAndConverterDataGrid;

    private void initRendererAndConverterDataGrid() {
        ColumnGenerator<User, Boolean> hasEmailGenerator = new ColumnGenerator<User, Boolean>() {
            @Override
            public Boolean getValue(ColumnGeneratorEvent<User> event) {
                return StringUtils.isNotEmpty(event.getItem().getEmail());
            }

            @Override
            public Class<Boolean> getType() {
                return Boolean.class;
            }
        };

        Converter<String, Boolean> converter = new Converter<String, Boolean>() {
            @Override
            public Boolean convertToModel(String value, Class<? extends Boolean> targetType, Locale locale) {
                // do nothing
                return null;
            }

            @Override
            public String convertToPresentation(Boolean value, Class<? extends String> targetType, Locale locale) {
                return BooleanUtils.isTrue(value)
                        ? FontAwesome.CHECK_SQUARE_O.getHtml()
                        : FontAwesome.SQUARE_O.getHtml();
            }

            @Override
            public Class<Boolean> getModelType() {
                return Boolean.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        };

        Column column = beforeRendererAndConverterDataGrid.addGeneratedColumn("hasEmail", hasEmailGenerator);
        column.setCaption("Has Email");

        column = noRendererAndConverterDataGrid.addGeneratedColumn("hasEmail", hasEmailGenerator);
        column.setCaption("Has Email");
        column.setConverter(converter);

        column = rendererAndConverterDataGrid.addGeneratedColumn("hasEmail", hasEmailGenerator);
        column.setCaption("Has Email");
        column.setConverter(converter);
        column.setRenderer(rendererAndConverterDataGrid.createRenderer(HtmlRenderer.class));
    }
}