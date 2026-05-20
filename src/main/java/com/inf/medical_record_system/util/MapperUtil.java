package com.inf.medical_record_system.util;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapperUtil {

    private final ModelMapper modelMapper;

    public MapperUtil(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public <S, D> D map(S source, Class<D> destinationClass) {
        return modelMapper.map(source, destinationClass);
    }

    public <S, D> List<D> mapList(List<S> sourceList, Class<D> destinationClass) {
        return sourceList
                .stream()
                .map(source -> map(source, destinationClass))
                .toList();
    }
}