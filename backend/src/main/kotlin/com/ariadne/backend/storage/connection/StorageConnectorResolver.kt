package com.ariadne.backend.storage.connection

import com.ariadne.backend.storage.domain.StorageSourceType
import org.springframework.stereotype.Component

/**
 * StorageSourceType에 맞는 StorageConnector를 선택한다.
 *
 * Spring에 등록된 Connector를 Type 기준으로 관리하며,
 * 동일 Type의 Connector가 둘 이상 등록되면 어떤 구현체를 사용할지
 * 모호해지므로 Application 시작 단계에서 실패시킨다.
 */
@Component
class StorageConnectorResolver(
    connectors: List<StorageConnector>,
) {

    /**
     * 동일한 Storage Type의 Connector가 둘 이상 등록되면
     * 어떤 구현체를 사용할지 모호하므로 시작 단계에서 실패시킨다.
     */
    private val connectorByType: Map<StorageSourceType, StorageConnector> =
        buildMap {
            connectors.forEach { connector ->
                val previous = put(connector.type, connector)

                check(previous == null) {
                    "Duplicate StorageConnector registered for type: ${connector.type}"
                }
            }
        }

    /**
     * 요청한 Storage Type을 담당하는 Connector 반환
     */
    fun resolve(type: StorageSourceType): StorageConnector {
        return connectorByType[type]
            ?: throw StorageConnectorNotFoundException(type)
    }
}