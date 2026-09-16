# File Management

## File Fragmentation

같은 목적의 파일이 여러 Storage와 Folder에 흩어진 상태를 File Fragmentation으로 본다.

예:

```text
Google Drive /취업/회사/채용공고.pdf
Windows      D:/Downloads/회사_이력서_v3.pdf
Windows      D:/Desktop/면접정리.txt
OneDrive     /backup/자기소개서.pdf
```

물리 위치는 다르지만 사용자의 관점에서는 하나의 주제일 수 있다.

## Topic Detection

초기 분석은 Metadata를 우선한다.

```text
File Name
Path
Storage Source
Modified Time
MIME Type
Size
```

Filename은 정답이 아니라 Signal이다.

```text
Filename Similarity
+
Path Similarity
+
Storage
+
Modified Time
+
File Type
```

## 초기 분석

```text
Filename
↓
Normalization
↓
Tokenization
↓
Noise Token Removal
↓
Keyword Extraction
↓
String Similarity
↓
Topic Candidate
↓
Fragmentation Score
```

AI/LLM을 먼저 사용하지 않는다.

## Duplicate와 Topic은 다른 문제

### Duplicate Detection

실제로 같은 콘텐츠인지 판단한다.

후보 Signal:

- Content Hash
- Size
- MIME Type

### Topic Clustering

서로 다른 File이 같은 목적/주제에 속하는지 판단한다.

후보 Signal:

- Filename
- Path
- Modified Time
- Storage Source

두 문제를 하나의 Similarity 로직으로 합치지 않는다.

## Organization Suggestion

정리 후보를 찾았다고 자동 이동하지 않는다.

제공 가능한 Action:

- 관련 File 보기
- Virtual Collection으로 묶기
- 중복 File 확인
- 한 Storage로 모으기
- 추천 무시

실제 이동은 명시적 사용자 선택 이후에 수행한다.

## Virtual Collection

원본 위치를 바꾸지 않고 논리적으로 관련 파일을 묶는 것이 기본 전략이다.

이 방식은 공유, 백업, 작업 중 파일처럼 현재 위치에 의미가 있는 파일을 Ariadne가 임의로 깨뜨리는 문제를 피한다.

## 평가

정리 기능은 알고리즘 구현만으로 완료 처리하지 않는다.

측정 대상:

- False Positive
- False Negative
- Metadata Signal별 정확도 변화
- 추천 수락/무시 비율
- 반복적으로 실패하는 Filename/Path 패턴

Semantic Similarity와 Content Analysis는 Metadata 기반 한계가 실제로 확인된 뒤 도입한다.
