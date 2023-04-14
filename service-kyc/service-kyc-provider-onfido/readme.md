# KYC provider Onfido

## Verification failed notification
There is a possibility to notify DWH when the requested check failed. To enable notification add **`uploaded_document_mail_dwh`** property with email to domain settings.
 - Template  **`match.address.dwh`** used cases when address match failed.
 - Template **`match.facial_similarity.dwh`** used when **facial_similarity_** report failed. 
