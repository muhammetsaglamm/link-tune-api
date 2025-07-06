package com.linktune.api.Service;

import com.linktune.api.Repository.LinkRepository;
import com.linktune.api.model.Link;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service // Bu sınıfın bir iş mantığı servisi olduğunu belirtir.
public class LinkService {

    private final LinkRepository linkRepository;

    // Constructor Injection: Spring, bu sınıfı oluştururken
    // bir LinkRepository'yi otomatik olarak buraya "enjekte eder".
    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    /**
     * Verilen uzun URL için benzersiz bir kısa kod oluşturur ve veritabanına kaydeder.
     * @param longUrl Kısaltılacak olan orijinal URL.
     * @return Veritabanına kaydedilmiş yeni Link nesnesi.
     */
    public Link shortenUrl(String longUrl) {
        // 7 karakterli, sadece harf ve rakamlardan oluşan rastgele bir kod üret.
        // Not: Gerçek bir uygulamada bu kodun daha önce kullanılmadığını kontrol etmek gerekir.
        String shortCode = RandomStringUtils.randomAlphanumeric(7);

        Link newLink = new Link();
        newLink.setLongUrl(longUrl);
        newLink.setShortCode(shortCode);

        // Oluşturulan bu nesneyi veritabanına kaydet ve sonucu geri döndür.
        return linkRepository.save(newLink);
    }
    @Cacheable(value = "links", key = "#shortCode")
    public Link getLongUrl(String shortCode) {
        System.out.println("!!! VERİTABANINA GİDİLİYOR: " + shortCode); // Bu logu test için ekledik
        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link not found with short code: " + shortCode));
    }
}