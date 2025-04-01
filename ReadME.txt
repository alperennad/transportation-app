Yolcu class'ım olmalı. Bu classtan 3 class türemeli :
-NormalYolcu
-OgrencıYolcu
-65YasYolcu

Arac class'ım olmalı. Bu classtan 3 tane class türemeli :
-Otobüs
-Tramvay
-Taksi

Odeme Yontemı class'ım olmalı. Bu sınıftan 3 tane class türemeli :
-Nakit
-Kart
-KentKart

Konum,Durak,Rota Hesaplayıcıları vb. yardımcı sınıflar olmalı.

JSON verilerini kullanılabilir hale getirip graph yapısı oluşturmalı.

Konumlar enlem ve boylam olarak verileceginden enlem ve boylamı verilen 2 nokta arasının
mesafesini hesaplayacak formül koda eklenmeli.

başlangıc noktası start_location ile bitis noktası end_location arasındaki yolda
hangi duragı kullanacagı bulunmalı.

start_location ile baslangıc duragı start_stop arasındaki mesafe 3km den fazla ise Taksi
az ise yürüyerek ulasmalı.

end_location a en yakın durak bulunup son durak end_stop olarak atanmalı
end_location ile end_stop arası 3km den az ise yürüyerek fazla ise Taksi ile
ulaşım sağlamalı.

mesafeler kus bakısı olarak hesaplanacaktır.

kullanıcıdan girdi olarak baslangıc noktasının enlem ve boylam bilgisi , hedef noktasının da enlem ve boylam bilgisi girdi olarak
istenecek.

hedef nokta koordinat girdisi de olabilir. İstenen herhangi bir durak ta olabilir.

cıktı olarak suna benzer bi cıktı cıkacak:
📍 Kullanıcı Konumuna En Yakın Durak: 
🔹 bus_otogar (400 m) → 🚶 Yürüme = 0 TL 
 
🚏 Rota Detayları: 
1⃣ bus_otogar → bus_sekapark (🚌 Otobüs) 
⏳ Süre: 10 dk 
💰 Ücret: 3 TL (Öğrenci %50 → 1.50 TL?) (Özel Gün → 0 TL?) 
2⃣ bus_sekapark → tram_sekapark (🔄 Transfer) 
⏳ Süre: 2 dk 
💰 Ücret: 0.50 TL 
3⃣ tram_sekapark → tram_halkevi (🚋 Tramvay) 
⏳ Süre: 8 dk 
💰 Ücret: 2.5 TL 
 
📊 Toplam: 
●  💰 Ücret: n TL 
●  ⏳ Süre: m dk 
●  📏 Mesafe: x km 
 
🛤 Alternatif Rotalar: 
🔹 🚖 Sadece Taksi (Daha hızlı, ancak maliyetli) 
🔹 🚍 Sadece Otobüs (Daha uygun maliyetli, ancak daha uzun sürebilir) 
🔹 🚋 Tramvay Öncelikli (Rahat ve dengeli bir ulaşım seçeneği) 
🔹 🛑 En Az Aktarmalı Rota (Daha az durak, daha az bekleme süresi)

Kullanıcı arayüzü olacak.JavaFX kullanılacak. girdiler ve çıktılar arayüz üzerinden sağlanacak. Veriler proje klasöründeki veriseti.json üzerinden
sağlanacak. Arayüzde harita olmayacak girdi ve çıktıları gösteren basit bir arayüz olacak.

40.76729967772186  29.89654641598463
40.754713846093593  29.959386549890038