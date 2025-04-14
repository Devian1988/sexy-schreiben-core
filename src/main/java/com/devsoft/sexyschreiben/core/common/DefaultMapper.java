package com.devsoft.sexyschreiben.core.common;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.nio.ByteBuffer;
import java.util.UUID;

public interface DefaultMapper<EntityClass, DTOClass, AdminDTOClass> {
    String BYTE_ARRAY_TO_UUID_MAPPER = "mapByteArrayToUUID";
    String STRING_TO_UUID_MAPPER = "mapStringToUUID";
    String UUID_TO_STRING_MAPPER = "mapUUIDToString";

    @Named(BYTE_ARRAY_TO_UUID_MAPPER)
    default UUID mapByteArrayToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }

    @Named(STRING_TO_UUID_MAPPER)
    default UUID mapStringToUUID(String string) {
        return string == null ? null : string.equals("0") ? UUID.randomUUID() : UUID.fromString(string);
    }

    @Named(UUID_TO_STRING_MAPPER)
    default String mapUUIDToString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @Mapping(target = "id", qualifiedByName = STRING_TO_UUID_MAPPER)
    EntityClass mapToEntity(AdminDTOClass adminDTO);

    @Mapping(target = "id", qualifiedByName = UUID_TO_STRING_MAPPER)
    AdminDTOClass mapToAdminDTO(EntityClass adminDTO);

    void updateToEntity(AdminDTOClass dto,
                        @MappingTarget EntityClass entity);
}
