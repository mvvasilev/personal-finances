package dev.mvvasilev.finances.services;

import dev.mvvasilev.common.exceptions.CommonFinancesException;
import dev.mvvasilev.finances.dtos.CreateUpdateWidgetDTO;
import dev.mvvasilev.finances.dtos.CreateWidgetParameterDTO;
import dev.mvvasilev.finances.dtos.WidgetDTO;
import dev.mvvasilev.finances.dtos.WidgetParameterDTO;
import dev.mvvasilev.finances.entity.Widget;
import dev.mvvasilev.finances.entity.WidgetParameter;
import dev.mvvasilev.finances.persistence.WidgetParameterRepository;
import dev.mvvasilev.finances.persistence.WidgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WidgetService {

    private final WidgetRepository widgetRepository;

    private final WidgetParameterRepository widgetParameterRepository;

    public WidgetService(WidgetRepository widgetRepository, WidgetParameterRepository widgetParameterRepository) {
        this.widgetRepository = widgetRepository;
        this.widgetParameterRepository = widgetParameterRepository;
    }

    public long createWidget(CreateUpdateWidgetDTO dto, int userId) {
        final var savedWidget = widgetRepository.saveAndFlush(mapWidget(new Widget(), userId, dto));

        final var params = dto.parameters()
                .stream()
                .map(p -> mapWidgetParameter(new WidgetParameter(), savedWidget.getId(), p))
                .toList();

        widgetParameterRepository.saveAllAndFlush(params);

        return savedWidget.getId();
    }

    public int updateWidget(Long id, CreateUpdateWidgetDTO dto) {
        var widget = widgetRepository.findById(id);

        if (widget.isEmpty()) {
            throw new CommonFinancesException("No widget with id %d exists.", id);
        }

        widgetRepository.saveAndFlush(mapWidget(widget.get(), widget.get().getUserId(), dto));

        return 1; // TODO: fetch rows affected from database
    }

    public int deleteWidget(Long id) {
        widgetRepository.deleteById(id);

        return 1; // TODO: fetch rows affected from database
    }

    private Widget mapWidget(Widget widget, int userId, CreateUpdateWidgetDTO dto) {
        widget.setName(dto.name());
        widget.setType(dto.type());
        widget.setUserId(userId);
        widget.setPositionX(dto.positionX());
        widget.setPositionY(dto.positionY());
        widget.setSizeX(dto.sizeX());
        widget.setSizeY(dto.sizeY());

        return widget;
    }

    private WidgetDTO mapWidgetDTO(Widget widget, List<WidgetParameterDTO> params) {
        return new WidgetDTO(
                widget.getId(),
                widget.getName(),
                widget.getPositionX(),
                widget.getPositionY(),
                widget.getSizeX(),
                widget.getSizeY(),
                widget.getType(),
                params
        );
    }

    private WidgetParameter mapWidgetParameter(WidgetParameter widgetParameter, Long widgetId, CreateWidgetParameterDTO dto) {
        widgetParameter.setWidgetId(widgetId);
        widgetParameter.setName(dto.name());
        widgetParameter.setBooleanValue(dto.booleanValue());
        widgetParameter.setNumericValue(dto.numericValue());
        widgetParameter.setTimestampValue(dto.timestampValue());
        widgetParameter.setStringValue(dto.stringValue());

        return widgetParameter;
    }

    private WidgetParameterDTO mapWidgetParameterDTO(WidgetParameter widgetParameter) {
        return new WidgetParameterDTO(
                widgetParameter.getWidgetId(),
                widgetParameter.getName(),
                widgetParameter.getStringValue(),
                widgetParameter.getNumericValue(),
                widgetParameter.getTimestampValue(),
                widgetParameter.getBooleanValue()
        );
    }

    public Collection<WidgetDTO> fetchAllForUser(int userId) {
        final var widgets = widgetRepository.findAllByUserId(userId);
        final var widgetParams = widgetParameterRepository.findAllByWidgetIdIn(widgets.stream().map(Widget::getId).toList());

        return widgets.stream().map(w -> {
                    final var paramDTOs = widgetParams.stream()
                            .filter(wp -> wp.getWidgetId() == w.getId())
                            .map(this::mapWidgetParameterDTO)
                            .toList();

                    return mapWidgetDTO(w, paramDTOs);
                })
                .toList();
    }
}
