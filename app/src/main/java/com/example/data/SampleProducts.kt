package com.example.data

import com.example.model.Product

object SampleProducts {
    val allProducts = listOf(
        Product(
            id = "lumina_anc_pro",
            title = "Aura Pro Wireless ANC Headphones",
            subtitle = "Lossless Audio • Spatial Sound • 45h Battery",
            category = "Audio & Tech",
            price = 199.99,
            originalPrice = 249.99,
            rating = 4.9,
            reviewCount = 1240,
            tags = listOf("Minimalist", "Tech & Futurism", "Best Seller", "Spatial Audio"),
            description = "Precision-engineered wireless headphones featuring hybrid active noise cancellation, custom 40mm beryllium drivers, memory foam earcups, and crystal-clear multipoint Bluetooth 5.4 connectivity.",
            highlights = listOf(
                "Active Noise Cancellation with Transparency Mode",
                "Lossless audio streaming over LDAC & aptX HD",
                "Up to 45 hours playtime with quick 10-min USB-C boost",
                "Ergonomic aerospace aluminum frame & plush vegan leather"
            ),
            specs = mapOf(
                "Driver Size" to "40mm Dynamic Beryllium",
                "Battery Life" to "45 hours (ANC off), 35 hours (ANC on)",
                "Weight" to "255g",
                "Connectivity" to "Bluetooth 5.4 / 3.5mm Aux",
                "Charging" to "USB-C Fast Charging"
            ),
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&q=80",
            colors = listOf("Midnight Matte", "Lunar Silver", "Espresso Bronze"),
            badge = "🔥 98% Match",
            styleVibe = "Tech & Futurism"
        ),
        Product(
            id = "chronox_smartwatch",
            title = "ChronoX Titanium Smartwatch",
            subtitle = "Sapphire Crystal • ECG & SpO2 • 14-Day Battery",
            category = "Wearables",
            price = 279.00,
            originalPrice = 329.00,
            rating = 4.8,
            reviewCount = 890,
            tags = listOf("Minimalist", "Luxury Elegance", "Health Tracker", "Waterproof 5ATM"),
            description = "A refined daily companion sculpted from grade-5 titanium. Features an ultra-bright AMOLED display, sapphire crystal glass, continuous heart health monitoring, and seamless contactless payments.",
            highlights = listOf(
                "Grade-5 Titanium case with anti-scratch sapphire face",
                "Always-On 1.43” AMOLED screen (1000 nits peak)",
                "Precision biometric sensor array with ECG & skin temperature",
                "5 ATM water resistance (swimming and diving ready)"
            ),
            specs = mapOf(
                "Display" to "1.43\" AMOLED 466x466",
                "Casing" to "Grade 5 Titanium & Ceramic",
                "Water Resistance" to "50 Meters (5 ATM)",
                "Battery" to "Up to 14 Days"
            ),
            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&q=80",
            colors = listOf("Raw Titanium", "Stealth Onyx", "Champagne Gold"),
            badge = "✨ Staff Pick",
            styleVibe = "Luxury Elegance"
        ),
        Product(
            id = "aeroflex_urban_pack",
            title = "AeroFlex Modular Commuter Backpack",
            subtitle = "Cordura Weatherproof • Magnetic Fidlock • 24L",
            category = "Wearables",
            price = 119.50,
            originalPrice = 145.00,
            rating = 4.7,
            reviewCount = 632,
            tags = listOf("Minimalist", "Urban Streetwear", "Waterproof", "Work & Travel"),
            description = "Engineered for city navigators and digital nomads. Crafted from tear-resistant Cordura ballistic nylon with rapid magnetic Fidlock buckles and a padded floating 16\" laptop sleeve.",
            highlights = listOf(
                "100% recycled weather-shield Cordura nylon exterior",
                "Dedicated TSA-friendly suspended 16\" laptop compartment",
                "Hidden RFID-blocking passport & card pocket",
                "Ergonomic airflow back panel with luggage trolley strap"
            ),
            specs = mapOf(
                "Capacity" to "24 Liters",
                "Dimensions" to "48 x 30 x 16 cm",
                "Weight" to "890g",
                "Laptop Fit" to "Up to 16\" MacBook Pro"
            ),
            imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80",
            colors = listOf("Charcoal Storm", "Obsidian Black", "Sage Olive"),
            badge = "🎒 Everyday Pro",
            styleVibe = "Minimalist"
        ),
        Product(
            id = "barista_pour_over_set",
            title = "Nordic Artisan Ceramic Pour-Over & Kettle",
            subtitle = "Matte Stoneware • Gooseneck Precision • Dual Wall",
            category = "Home & Living",
            price = 68.00,
            originalPrice = 85.00,
            rating = 4.9,
            reviewCount = 510,
            tags = listOf("Nordic Cozy", "Sustainable Eco", "Minimalist", "Artisan Made"),
            description = "Elevate morning rituals with hand-finished Scandinavian ceramic stoneware. Includes a thermal-retentive dripper cone, heat-resistant borosilicate glass carafe, and fine stainless mesh filter.",
            highlights = listOf(
                "Handcrafted stoneware with matte reactive ceramic glaze",
                "Precision pour spout engineered for optimal extraction bloom",
                "Includes reusable double-layer micro-mesh steel filter",
                "BPA-free, dishwasher safe, and thermal shock resistant"
            ),
            specs = mapOf(
                "Carafe Capacity" to "750 ml (3-4 cups)",
                "Material" to "High-fired Ceramic & Borosilicate Glass",
                "Origin" to "Copenhagen Design Studio"
            ),
            imageUrl = "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=800&q=80",
            colors = listOf("Nordic Chalk", "Earthy Sand", "Forest Basalt"),
            badge = "☕ Morning Ritual",
            styleVibe = "Nordic Cozy"
        ),
        Product(
            id = "lumina_smart_desk_lamp",
            title = "NeoGlow Smart Circadian Desk Lamp",
            subtitle = "Full-Spectrum Light • Qi2 Fast Wireless Base • Touch Slider",
            category = "Home & Living",
            price = 89.99,
            originalPrice = 110.00,
            rating = 4.8,
            reviewCount = 420,
            tags = listOf("Tech & Futurism", "Minimalist", "Workspace", "Qi2 Wireless"),
            description = "Natural circadian rhythm lighting tailored for peak productivity and tranquil evening wind-down. Features stepless color temperature controls, Ra98 high color rendering, and integrated 15W Qi2 wireless charger.",
            highlights = listOf(
                "98+ CRI natural sun simulation eliminates eye strain",
                "Seamless 2700K - 6500K dynamic circadian synchronization",
                "Fast 15W magnetic wireless charging pad integrated into heavy alloy base",
                "Precision dual-pivot aluminum arm with magnetic head tilt"
            ),
            specs = mapOf(
                "Luminance" to "1200 Lux at 40cm",
                "Color Range" to "2700K - 6500K",
                "Wireless Charge" to "15W Qi2 / MagSafe Compatible",
                "Build" to "Anodized Aluminum Alloy"
            ),
            imageUrl = "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&q=80",
            colors = listOf("Space Gray", "Sand Matte", "Pure White"),
            badge = "💡 Focus Booster",
            styleVibe = "Tech & Futurism"
        ),
        Product(
            id = "hydraglow_percussion_massager",
            title = "PulseFlex Mini Deep-Tissue Massager",
            subtitle = "Brushless QuietGlide • 4 Speed Levels • Pocket-Sized",
            category = "Wellness & Beauty",
            price = 74.99,
            originalPrice = 99.00,
            rating = 4.7,
            reviewCount = 740,
            tags = listOf("Wellness & Beauty", "Minimalist", "Recovery", "USB-C"),
            description = "Compact percussion massage therapy that fits in any gym bag or carry-on. Whisper-quiet brushless motor delivers up to 3200 RPM of deep muscle relief after workouts or long desk sessions.",
            highlights = listOf(
                "Ultra-quiet 38dB QuietGlide brushless motor",
                "High-torque percussion with 4 custom vibration intensities",
                "4 interchangeable therapeutic massage attachments",
                "Up to 5 hours runtime on a single USB-C charge"
            ),
            specs = mapOf(
                "Motor Speed" to "1800 - 3200 RPM",
                "Stroke Amplitude" to "10mm",
                "Weight" to "410g",
                "Battery" to "2500mAh Lithium-Ion"
            ),
            imageUrl = "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&q=80",
            colors = listOf("Deep Teal", "Midnight Slate", "Blush Petal"),
            badge = "🧘 Rest & Recovery",
            styleVibe = "Minimalist"
        ),
        Product(
            id = "merino_knit_sweater",
            title = "Nordic Pure Merino Wool Knit",
            subtitle = "100% Extra-Fine Merino • Odor-Resistant • Thermal Regulating",
            category = "Wearables",
            price = 125.00,
            originalPrice = 160.00,
            rating = 4.9,
            reviewCount = 310,
            tags = listOf("Nordic Cozy", "Luxury Elegance", "Sustainable Eco", "Autumn/Winter"),
            description = "Crafted from ethically sourced 19.5-micron extra-fine merino wool. Naturally breathable, silky-soft against sensitive skin, and temperature-regulating in both crisp mornings and indoor air-conditioning.",
            highlights = listOf(
                "100% RWS certified sustainable extra-fine merino wool",
                "Classic seamless rib-knit collar, cuffs, and hem",
                "Naturally moisture-wicking and antibacterial",
                "Zero synthetic fillers for timeless durability"
            ),
            specs = mapOf(
                "Material" to "100% Extrafine Merino Wool",
                "Fibers" to "19.5 Microns",
                "Care" to "Hand Wash or Wool Cycle"
            ),
            imageUrl = "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=800&q=80",
            colors = listOf("Heather Oatmeal", "Deep Navy", "Forest Moss"),
            badge = "🐑 Cozy Essential",
            styleVibe = "Nordic Cozy"
        ),
        Product(
            id = "lumina_magsafe_powerbank",
            title = "AuraSnap 10,000mAh Magnetic Power Core",
            subtitle = "Qi2 15W Wireless • Bi-directional 30W USB-C • Kickstand",
            category = "Audio & Tech",
            price = 49.99,
            originalPrice = 64.99,
            rating = 4.8,
            reviewCount = 980,
            tags = listOf("Tech & Futurism", "Minimalist", "Travel Ready", "Qi2"),
            description = "Ultra-slim magnetic power bank designed with strong N52 neodymium magnets. Snaps firmly onto your phone while the fold-out zinc kickstand props your screen in portrait or landscape.",
            highlights = listOf(
                "15W Qi2 certified fast wireless output",
                "30W USB-C PD power delivery charges tablets and phones at top speed",
                "Aerospace zinc-alloy kickstand with stepless angle adjustment",
                "Smart LED percentage display shows remaining battery"
            ),
            specs = mapOf(
                "Capacity" to "10,000mAh / 38.5Wh",
                "Wireless Output" to "5W / 7.5W / 10W / 15W",
                "Wired Output" to "PD 30W Max",
                "Thickness" to "14.8mm"
            ),
            imageUrl = "https://images.unsplash.com/photo-1609592426815-e2a22287f340?w=800&q=80",
            colors = listOf("Titanium Gray", "Midnight Matte", "Arctic Frost"),
            badge = "⚡ Power On the Go",
            styleVibe = "Tech & Futurism"
        )
    )

    fun getById(id: String): Product? = allProducts.find { it.id == id }
}
