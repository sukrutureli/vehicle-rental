Araç Takip & Kiralama Servisi - README

----------------------------------

1) Mimari kararlar ve nedenleri
- Spring Boot + Java 11
Hızlı geliştirme ve yaygın ekosistem desteği için Spring Boot tercih edildi. Web, Data JPA, Validation kütüphaneleriyle CRUD + filtreleme kolayca kuruldu.

- Veritabanı
Development aşamasında H2 in-memory DB kullanıldı, hızlı deneme/test için. Test için DataInitializer class'ı yazıldı, db boşsa rastgele araç ve müşteri üreiyor.

- Katmanlı yapı
Controller - Repo - Entity şeklinde basit ama anlaşılır bir katmanlı mimari kullanıldı.
Service katmanı eklenebilirdi ama kapsamı şişirmemek adına controller içinde çözüldü.

- Exception handling
Global bir @ControllerAdvice ile hatalar JSON formatında döndürülüyor. Bu sayede frontend tarafında hatalar daha net görülebiliyor.

- Filtreleme & sayfalama & sıralama
Araç ve kiralama listelerinde @RequestParam tabanlı query parametrelerle filtreleme eklendi. Page & size parametreleri ile basit sayfalama, sortBy/sortDir parametreleri ile sıralama destekleniyor.

- Konum takibi
Araçların son bilinen konumu VehicleLocation tablosunda tutuluyor. Her araç için latest endpoint’i var. Ayrıca tüm araçların son konumlarını listeleyen endpoint mevcut.
Konum ekleme için /api/telematics/{vehicleId}/location endpoint’i var.

- Dummy servis
@Scheduled anotasyonu ile dakikada bir rastgele yakın koordinat üretilebilir. Demo/test amaçlı çalışıyor.

- Frontend
Minimal HTML + JS sayfaları var (vehicles.html, rentals.html, customers.html, locations.html). Tamamen basit fetch() çağrılarıyla API’ye bağlanıyor.

----------------------------------

2) Proje yapısı - Alınan kararlar

Controller, entity ve repo package'larından oluşmaktadır. Kullanıcı arayüzü için HTML ve JS kullanıldı, bu sayede api'ler kullanıcı arayüzünden test edilebiiyor.
Vehicle:
- Entity: Plate değişkeni her araç için unique olarak düşünüldü. VehicleStatus için AVAILABLE, RENTED, MAINTENANCE değerleri oluşturuldu. Transmission için MANUAL, AUTOMATIC değerleri oluşturuldu. FuelType için GASOLINE, DIESEL, ELECTRIC, HYBRID değerleri oluşturuldu. Bu enumerationlar db'de string olarak saklanmaktadır, enum sırasının değişmesi ihtimaline karşın enum value saklanmadı.
- Controller: List, filter, pageing, sorting ve CRUD işlemleri bu class'ta yapıldı. LocalDateTime değişkeni için saniye ve salise değerlerinin önemsiz olduğu düşünülerek db'ye kaydederken dakikaya kadar hassasiyetle kaydetmeyi sağlayan bir yapı controller kısmında eklendi.
- Repo: Plate değişkeni unique olarak düşünüldüğü için bu class'ta plate kullanarak yapılabilecek sorgularla alakalı metot eklendi.
- vehicles.html: Controller'da geliştirilen api'lerin testi yapılıyor. JS fetch işlemi yapılyor.
- edit-vehicle.html: Update apisinin testi yapılıyor. JS fetch işlemi yapılyor.

Customer:
- Entity: Email değişkeni her customer için unique olarak düşünüldü. 
- Controller: List ve CRUD işlemleri bu class'ta yapıldı.
- Repo: Email değişkeni unique olarak düşünüldüğü için bu class'ta email kullanarak yapılabilecek sorgularla alakalı metot eklendi.
- customers.html: Controller'da geliştirilen api'lerin testi yapılıyor. JS fetch işlemi yapılyor.
- edit-customer.html: Update apisinin testi yapılıyor. JS fetch işlemi yapılyor.

Rental:
- Entity: Vehicle ve customer objeleri yerine id'leri tutuldu. Kullanıcı arayüzünde kullanıcı inputu almak için id'ler uygun olmadığı için vehicle için plate, customer için email @transient değişkenleri kullanıldı. RentalStatus için ACTIVE, COMPLETED, CANCELLED değerleri oluşturuldu. LocalDateTime değişkeni için saniye ve salise değerlerinin önemsiz olduğu düşünülerek db'ye kaydederken dakikaya kadar hassasiyetle kaydetmeyi sağlayan bir yapı controller kısmında eklendi.
- Controller: List, filter, add, complete ve cancel işlemleri bu class'ta yapıldı. Complete PUT işlemi ilgili rental'de RentalStatus'u COMPLETED değerine set eder. Cancel PUT işlemi ilgili rental'de RentalStatus'u CANCELLED değerine set eder. Add api'sinde çakışma kontrolü yapıldı. Add işlemi başarılı olduğunda Vehicle tablosunda ilgili araç VehicleStatus RENTED olarak güncelleniyor.
- Repo: Çakışma kontrolü yapabilmek için gerekli bir metot eklendi.
- rentals.html: Controller'da geliştirilen api'lerin testi yapılıyor. JS fetch işlemi yapılyor.

VehicleLocation: 
- Entity: Vehicle objesi yerine id'si tutuldu. Kullanıcı arayüzünde kullanıcı inputu almak için id'ler uygun olmadığı için vehicle için plate @transient değişkeni kullanıldı. LocalDateTime değişkeni için saniye ve salise değerlerinin önemsiz olduğu düşünülerek db'ye kaydederken dakikaya kadar hassasiyetle kaydetmeyi sağlayan bir yapı controller kısmında eklendi.
- Controller: Aracın son bilinen konumu, tüm araçların son bilinen konumları ve konum bildirimi apileri geliştirildi. Tüm araçların son bilinen konumları listesinde nearLat, nearLon ve radiusKm'ye göre filtreleme özelliği eklendi, Haversine formülü kullanıldı.
- Repo: Son bilinen konumu sorgulamak için metot eklendi.
- locations.html: Controller'da geliştirilen api'lerin testi yapılıyor. JS fetch işlemi yapılyor.

DataInitializer: DB boş ise uygulama açıldığında test amaçlı random vehicle ve customer üreiyor.

GlobalExceptionHandler: @ControllerAdvice ile hatalar JSON olarak döndürülüyor.

DummyLocationGenerator: 60 sn'de 1 her bir vehicle'ın location bilgisini değiştiriyor. Bunu VehicleLocation tablosuna yazıyor.

index.html: Entity'lerin linkleri mevcut.

----------------------------------

3) Kurulum ve Çalıştırma adımları

- Repoyu clone'layın. (https://github.com/sukrutureli/vehicle-rental.git)

- Projeyi içe aktar
	Eclipse’i açılır.
	File > Import... > Existing Maven Projects seçilir.
	Proje klasörünü (vehicle-rental) seçilir ve içeri alınır.
	Eclipse otomatik olarak Maven bağımlılıklarını indirecek.

- Veritabanı ayarları
	Varsayılan olarak proje H2 in-memory DB kullanıyor.
	Yani ekstra kurulum gerekmiyor, uygulama açıldığında H2 devreye giriyor.

- Uygulamayı çalıştır
	Eclipse’te VehicleRentalApplication.java dosyası bulunur.
	Sağ tık -> Run As -> Java Application.
	Konsolda Started VehicleRentalApplication mesajı görüldüğünde backend ayağa kalkmış olur.
	Varsayılan port: http://localhost:8080

- Arayüze erişim
	Tarayıcıdan http://localhost:8080/ adresine gidilir.

- Jar dosyası üretmek
	Proje root -> Sağ tık -> Run as -> Maven build.
	target/vehicle-rental-0.0.1-SNAPSHOT.jar oluşur.
	Çalıştırmak için:
	java -jar target/vehicle-rental-0.0.1-SNAPSHOT.jar

----------------------------------

4) Çevresel değişkenler

DB_URL: jdbc url (jjdbc:h2:file:./data/rentaldb)

DB_USER: sa

DB_PASS: 

----------------------------------

5) Örnek istekler ve yanıtlar

- Vehicle
GET http://localhost:8080/api/vehicles?page=0&size=5&fuelType=GASOLINE&transmission=AUTOMATIC&status=AVAILABLE

[
    {
        "id": "9683d8f6-55dd-4f6b-906a-e7f79c1184da",
        "plate": "03USH681",
        "brand": "Renault",
        "model": "Clio",
        "city": "Izmir",
        "dailyPrice": 259,
        "status": "AVAILABLE",
        "transmission": "AUTOMATIC",
        "fuelType": "GASOLINE",
        "availableFrom": "2025-10-06T11:05:00",
        "availableTo": "2025-10-17T11:05:00",
        "createdAt": "2025-10-03T11:05:00",
        "updatedAt": "2025-10-03T11:05:00"
    },
    {
        "id": "9c30a3c8-7058-4067-b5ec-9c5ff111b5f7",
        "plate": "65FPI797",
        "brand": "BMW",
        "model": "X5",
        "city": "Ankara",
        "dailyPrice": 613,
        "status": "AVAILABLE",
        "transmission": "AUTOMATIC",
        "fuelType": "GASOLINE",
        "availableFrom": "2025-10-04T11:05:00",
        "availableTo": "2025-10-10T11:05:00",
        "createdAt": "2025-10-03T11:05:00",
        "updatedAt": "2025-10-03T11:05:00"
    }
]

- Customer
POST /api/customers
{
    "id": "601c0f73-d06e-4f79-be22-094a1ee1363a",
    "firstName": "Şükrü",
    "lastName": "Türeli",
    "email": "sukrutureli@gmail.com",
    "phone": "05438083076"
}

- Rental
PUT /api/rentals/89dde826-1158-4a9f-bd58-4fa906c78b5e/complete

- VehicleLocation
POST /api/telematics/db0fc703-d966-4378-9d87-ac620abb20ea/location
{
	"id": "3c39976e-3818-4624-b32a-e06e3258218c",
	"lat": 39,
	"lon": 32,
	"reportedAt": "2025-10-03T11:30:00",
	"vehicleId": "db0fc703-d966-4378-9d87-ac620abb20ea",
	"vehiclePlate": "13ISY963"
}

----------------------------------

6) Bilinen kısıtlar ve gelecek geliştirme önerileri

- JWT/rol tabanlı yetkilendirme uygulanmadı, apiler serbest.
- Konum servisinde SSE/WebSocket yok. Şu an sadece REST çağrıları var.
- Dummy servis random veri üretiyor ama daha gerçekçi senaryolar için iyileştirilebilir.
- UI tamamen basit HTML/JS. Daha profesyonel bir frontend (React/Angular) eklenebilir.
- GitHub Actions pipeline, JaCoCo coverage raporu eklenebilir.
- 60 sn'de en az bir yeni entry eklenen VehicleLocation tablosu için iyileştirme düşünülebilir.

----------------------------------
