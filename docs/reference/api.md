# API Reference

이 문서는 구현된 Backend HTTP API의 계약만 기록한다.

아직 구현하지 않은 Endpoint를 계획만으로 추가하지 않는다.

각 API에는 최소한 다음을 기록한다.

- Method / Path
- Request
- Response
- Error
- Authentication
- DB Side Effect
- 외부 I/O 여부

Android가 Provider API를 직접 호출하는 동작은 Backend API 문서에 넣지 않는다.

Backend API가 새로 추가될 때 코드와 같은 변경에서 이 문서를 갱신한다.

## Metadata Ingestion

- **Method / Path**: `POST /api/storage-sources/{storageSourceId}/metadata`
- **Request**:
  ```json
  {
    "files": [
      {
        "externalId": "string (required)",
        "name": "string (required)",
        "mimeType": "string (required)",
        "size": "number >= 0 (required)",
        "path": "string | null",
        "modifiedAt": "ISO-8601 Instant | null"
      }
    ]
  }
  ```
  Android가 Provider(Google Drive 등) 응답을 공통 포맷으로 변환한 뒤 전달한다. Provider 고유 필드는 포함하지 않는다.
- **Response**: `200 OK`, Body 없음.
- **Error**:
  - 요청 필드 검증 실패(`externalId`/`name`/`mimeType` 공백, `size` 음수 등) → `400 Bad Request`
  - `storageSourceId`에 해당하는 StorageSource가 없음 → `404 Not Found` (`ProblemDetail`)
- **Authentication**: 현재 미적용(기존 `/api/storage-sources/{id}/sync`와 동일한 수준).
- **DB Side Effect**: `(storageSourceId, externalId)` 기준으로 `File`/`FileLocation`을 신규 생성하거나 기존 행을 갱신(name/mimeType/size/path/modifiedAt)하고, `StorageSource.lastSyncedAt`을 갱신한다. 동일 `externalId` 재전송은 중복 Row를 만들지 않는다.
- **외부 I/O 여부**: 없음. Android가 이미 Provider에서 조회/변환한 Metadata를 전달받아 저장만 수행한다(기존 `/sync`의 Pull 방식과 달리 Backend가 Provider를 직접 호출하지 않는다).
