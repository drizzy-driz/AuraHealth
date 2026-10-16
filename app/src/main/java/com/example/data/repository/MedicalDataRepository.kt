package com.example.data.repository

import com.example.data.model.AssessmentRequest
import com.example.data.model.AssessmentResult
import com.example.data.model.ConditionMatch
import com.example.data.model.HomeRemedy
import com.example.data.model.MedicalTerm
import com.example.data.model.Symptom

object MedicalDataRepository {

    val symptomsList = listOf(
        Symptom("scaling_lips", "Scaling / Peeling Lips", "Lips & Mouth", "lip"),
        Symptom("cracked_corners", "Cracked Lip Corners", "Lips & Mouth", "lip"),
        Symptom("dry_mouth", "Dry Mouth / Sticky Saliva", "Lips & Mouth", "oral"),
        Symptom("bleeding_gums", "Bleeding / Sensitive Gums", "Lips & Mouth", "oral"),
        Symptom("mouth_ulcers", "Painful Mouth Sores / Ulcers", "Lips & Mouth", "oral"),
        Symptom("skin_bumps", "Red Skin Bumps / Pimples", "Skin & Face", "skin"),
        Symptom("razor_irritation", "Razor Bumps / Shave Burn", "Skin & Face", "grooming"),
        Symptom("excessive_dryness", "Excessive Skin Dryness", "Skin & Face", "skin"),
        Symptom("itching", "Intense Skin Itching (Pruritus)", "Skin & Face", "skin"),
        Symptom("redness", "Facial or Body Redness / Flushing", "Skin & Face", "skin"),
        Symptom("flaking_patches", "Flaking / Yellowish Scaly Patches", "Scalp & Hair", "scalp"),
        Symptom("headache_fatigue", "Headache & Systemic Fatigue", "General", "wellness"),
        Symptom("dark_urine", "Dark Urine / Infrequent Urination", "General", "wellness"),
        Symptom("eyelid_crusting", "Eyelid Redness / Crusting", "Skin & Face", "eye")
    )

    val medicalTerms: List<MedicalTerm> = listOf(
        MedicalTerm(
            id = "angular_cheilitis",
            name = "Angular Cheilitis",
            alternativeName = "Perlèche / Corner Lip Fissures",
            category = "Oral & Lips",
            anatomicalRegion = "Corners of the mouth (oral labial commissures)",
            definition = "Inflammatory condition marked by painful erythema, scaling, and painful fissures localized specifically to the labial commissures of the mouth.",
            primarySymptoms = listOf(
                "Painful cracking or fissures at lip corners",
                "Maceration (white, soggy skin)",
                "Redness and localized burning sensation",
                "Bleeding or crusting upon opening mouth wide"
            ),
            commonCauses = listOf(
                "Saliva pooling in lip folds creating a moist environment",
                "Secondary Candida albicans (yeast) or Staphylococcus aureus colonization",
                "Lip licking, ill-fitting dental appliances, or nutritional deficiencies (Iron, B12)"
            ),
            atHomeCare = listOf(
                "Apply thick petrolatum ointment or zinc oxide barrier paste at bedtime",
                "Keep mouth corners completely dry; strictly avoid lip licking",
                "Over-the-counter 1% clotrimazole antifungal cream mixed with 1% hydrocortisone for mild flare-ups",
                "Sanitize lip balms or replace used lip products to prevent reinfection"
            ),
            redFlags = "Spreading warmth, pus drainage, facial swelling, or non-healing fissures persisting beyond 14 days.",
            preventionTip = "Protect lip angles with occlusive petrolatum before brushing teeth or going to sleep."
        ),
        MedicalTerm(
            id = "cheilitis",
            name = "Cheilitis (Lip Dryness & Chapping)",
            alternativeName = "Exfoliative Cheilitis / Chapped Lips",
            category = "Oral & Lips",
            anatomicalRegion = "Vermilion border of lips",
            definition = "Acute or chronic inflammation and barrier dysfunction of the vermilion zone of the lips, causing continuous flaking, scaling, and soreness.",
            primarySymptoms = listOf(
                "Dry, peeling, or cracked lip surface",
                "Sensation of tightness and stinging",
                "Vertical fissures that may bleed",
                "Rough, sandpaper-like texture"
            ),
            commonCauses = listOf(
                "Compromised lipid barrier from harsh weather or dry indoor air",
                "Compulsive lip licking (digestive enzymes degrade mucosal barrier)",
                "Allergens in lip balms (fragrances, menthol, camphor, beeswax, cinnamates)",
                "Sun (UV radiation) exposure without SPF protection"
            ),
            atHomeCare = listOf(
                "Apply 100% white petrolatum or ceramide ointment generously 4-6 times daily",
                "Slather a thick layer of ointment immediately before sleep (night slugging)",
                "Eliminate irritating lip products containing camphor, phenol, menthol, or citrus flavors",
                "Use a mineral zinc oxide SPF 30 lip balm during daytime sun exposure"
            ),
            redFlags = "Firm persistent ulcer, numbness of lip, thick white hyperkeratotic plaque, or rapid ulceration.",
            preventionTip = "Never tear peeling lip skin with teeth or fingers; apply ointment and allow natural shedding."
        ),
        MedicalTerm(
            id = "contact_dermatitis",
            name = "Contact Dermatitis",
            alternativeName = "Allergic or Irritant Eczematous Reaction",
            category = "Skin & Face",
            anatomicalRegion = "Face, neck, hands, or exposed skin surfaces",
            definition = "Inflammatory cutaneous eruption triggered either by direct chemical/physical damage to the skin barrier (irritant) or a delayed type-IV hypersensitivity immune response (allergic).",
            primarySymptoms = listOf(
                "Sharply demarcated erythema (redness)",
                "Intense burning, stinging, or pruritus (itching)",
                "Tiny fluid-filled vesicles or localized swelling",
                "Dry, leathery, cracked skin in chronic stages"
            ),
            commonCauses = listOf(
                "Fragrances, essential oils, or preservatives (methylisothiazolinone) in cosmetics",
                "Harsh sulfates (SLS), aftershave alcohol, or chemical hair dyes",
                "Nickel or metal jewelry contact",
                "Poison ivy, plant resin, or industrial cleaning solvents"
            ),
            atHomeCare = listOf(
                "Wash immediately with lukewarm water and mild soap-free synthetic cleanser",
                "Apply cold damp milk or saline compresses for 10-15 minutes to reduce flare",
                "Apply 1% hydrocortisone cream twice daily for up to 5-7 days",
                "Apply fragrance-free bland ceramide cream (e.g. Vanicream or CeraVe) to restore barrier"
            ),
            redFlags = "Facial swelling involving eyelids or lips, blistering covering >10% of body, or signs of bacterial infection (honey crusts).",
            preventionTip = "Perform a 48-hour patch test inside the forearm before using any new facial or beard grooming product."
        ),
        MedicalTerm(
            id = "folliculitis",
            name = "Folliculitis & Razor Bumps",
            alternativeName = "Pseudofolliculitis Barbae / Shaving Bumps",
            category = "Skin & Face",
            anatomicalRegion = "Beard area, neckline, scalp, and shaven zones",
            definition = "Inflammation or foreign body reaction in hair follicles, frequently occurring when curved coarse facial hairs curl back and pierce surrounding skin after close shaving.",
            primarySymptoms = listOf(
                "Tender red papules or small white pustules around hair follicles",
                "Pruritus (itching) and burning in shaved zones",
                "Hyperpigmentation (dark spots) following bump healing",
                "Hair visibly trapped under the skin surface"
            ),
            commonCauses = listOf(
                "Shaving against the grain with multi-blade cartridge razors",
                "Stretching skin taut during shaving",
                "Bacterial invasion (Staphylococcus aureus) from dull or dirty razors",
                "Tight collars causing friction against sweaty neck skin"
            ),
            atHomeCare = listOf(
                "Rest the skin: pause wet shaving for 48-72 hours or switch to an electric clipper with a guard",
                "Apply a warm, moist towel for 5 minutes before grooming to soften hair shafts",
                "Shave strictly in the direction of hair growth using a single-blade razor",
                "Use 1-2% salicylic acid wash or 2.5% benzoyl peroxide cleanser to unclog follicular ostia",
                "Apply soothing aloe vera and non-comedogenic hydration"
            ),
            redFlags = "Rapidly expanding hot red boil (furuncle), carbuncle cluster, or fever.",
            preventionTip = "Never pluck ingrown hairs with sharp tweezers; use a warm compress and gentle exfoliation."
        ),
        MedicalTerm(
            id = "xerostomia",
            name = "Xerostomia (Dry Mouth)",
            alternativeName = "Salivary Hypofunction",
            category = "Oral & Lips",
            anatomicalRegion = "Oral cavity, tongue, and buccal mucosa",
            definition = "Subjective sensation of dryness in the oral cavity, commonly associated with reduced salivary volume or altered saliva composition necessary for oral defense.",
            primarySymptoms = listOf(
                "Sticky, dry oral feeling or ropey saliva",
                "Frequent thirst, especially when awakening",
                "Difficulty chewing dry crackers, swallowing, or speaking clearly",
                "Rough, red, pebbled tongue and altered taste perception"
            ),
            commonCauses = listOf(
                "Medication side effects (antihistamines, decongestants, antidepressants, diuretics)",
                "Chronic mouth breathing during sleep or obstructive sleep apnea",
                "Systemic dehydration or excessive caffeine/alcohol intake",
                "Salivary gland conditions or radiation therapy"
            ),
            atHomeCare = listOf(
                "Sip water frequently throughout the day; keep a water flask at bedside",
                "Chew 100% xylitol gum or suck on xylitol mints to mechanically stimulate salivary glands",
                "Use specialized dry mouth oral rinses (alcohol-free, enzyme-containing)",
                "Run a cool mist bedroom humidifier during sleeping hours"
            ),
            redFlags = "Rapidly accelerating tooth decay, severe oral fungal infection (thrush), or hard salivary gland swelling.",
            preventionTip = "Strictly avoid alcohol-based mouthwashes that further desiccate mucosal tissues."
        ),
        MedicalTerm(
            id = "dehydration",
            name = "Systemic Dehydration",
            alternativeName = "Hypovolemia / Fluid Deficit",
            category = "Body & Systemic",
            anatomicalRegion = "Whole body, cellular physiology, and renal system",
            definition = "Deficit of total body water that disrupts normal cellular osmolarity, vascular volume, and mucosal barrier lubrication.",
            primarySymptoms = listOf(
                "Dark amber-colored urine and low voiding frequency",
                "Dry sticky lips, sunken eyes, and sluggish skin turgor",
                "Dull tension headache and cognitive brain fog",
                "Orthostatic dizziness upon standing quickly"
            ),
            commonCauses = listOf(
                "Inadequate baseline fluid consumption during high heat or prolonged indoor air conditioning",
                "Vigorous physical exercise without electrolyte replenishment",
                "Gastrointestinal illness (vomiting/diarrhea) or febrile illness",
                "Excessive consumption of diuretic beverages (energy drinks, alcohol)"
            ),
            atHomeCare = listOf(
                "Drink oral rehydration solutions (ORS) combining water, sodium, and glucose for optimal cellular absorption",
                "Sip small amounts (150-200ml) every 15 minutes rather than chugging massive volumes at once",
                "Eat hydrating foods with high water content (cucumber, watermelon, bone broth)",
                "Rest in a cool, shaded environment"
            ),
            redFlags = "Confusion, lethargy, fainting, inability to keep fluids down, lack of urination for >8 hours, or rapid heart rate.",
            preventionTip = "Check morning urine color: pale straw indicates optimal hydration."
        ),
        MedicalTerm(
            id = "seborrheic_dermatitis",
            name = "Seborrheic Dermatitis",
            alternativeName = "Dandruff / Facial Seborrhea",
            category = "Skin & Face",
            anatomicalRegion = "Scalp, eyebrows, nasolabial folds, beard area, chest",
            definition = "Common chronic superficial dermatosis affecting sebum-rich areas, driven by an inflammatory response to Malassezia yeast species.",
            primarySymptoms = listOf(
                "Greasy, yellowish, or powdery white flakes on scalp and eyebrows",
                "Erythema (redness) along sides of nose and beard roots",
                "Mild to moderate pruritus (itching) and skin tightness",
                "Flaking exacerbated by cold dry winter or emotional stress"
            ),
            commonCauses = listOf(
                "Overgrowth or altered sensitivity to commensal Malassezia yeast",
                "High sebum production providing yeast nutrient substrate",
                "Disrupted epidermal barrier and immune reaction"
            ),
            atHomeCare = listOf(
                "Use anti-dandruff shampoo containing 1-2% ketoconazole, zinc pyrithione, or selenium sulfide",
                "Lather active shampoo into beard and scalp, leave on for 3-5 minutes before rinsing",
                "Apply lightweight, oil-free moisturizer formulated with squalane or ceramides",
                "Avoid heavy hair pomades or coconut oil which feed Malassezia yeast"
            ),
            redFlags = "Widespread erythroderma, secondary bacterial infection with crusting, or failure to respond to medicated cleansers.",
            preventionTip = "Alternate between two active anti-fungal shampoos weekly to prevent yeast resistance."
        ),
        MedicalTerm(
            id = "gingivitis",
            name = "Gingivitis",
            alternativeName = "Early Periodontal Inflammation",
            category = "Oral & Lips",
            anatomicalRegion = "Marginal gingiva and interdental papillae",
            definition = "Reversible plaque-induced inflammation of the marginal gingiva without loss of periodontal attachment or underlying alveolar bone.",
            primarySymptoms = listOf(
                "Swollen, puffy, or dark red gums (loss of healthy pink stippling)",
                "Bleeding upon gentle flossing or toothbrushing",
                "Persistent mild halitosis (oral odor)",
                "Tenderness when chewing tough foods"
            ),
            commonCauses = listOf(
                "Microbial biofilm (dental plaque) accumulation at gingival margin",
                "Skipping interdental cleaning (flossing / interdental brush)",
                "Smoking or tobacco chewing reducing gingival blood perfusion",
                "Hormonal fluctuations or mouth breathing during sleep"
            ),
            atHomeCare = listOf(
                "Floss gently once daily using the 'C-shape' curve around each tooth root",
                "Brush teeth twice daily with soft-bristle toothbrush at 45-degree angle toward gum line",
                "Rinse with warm saline solution (1/2 tsp salt in 1 cup warm water) for 30 seconds",
                "Use an anti-gingivitis mouthwash containing cetylpyridinium chloride (CPC) or essential oils"
            ),
            redFlags = "Loose permanent teeth, receding gum lines exposing yellow roots, purulent discharge (pus) between tooth and gum.",
            preventionTip = "Floss before brushing so fluoride toothpaste can coat the interproximal spaces."
        ),
        MedicalTerm(
            id = "halitosis",
            name = "Halitosis (Oral Malodor)",
            alternativeName = "Volatile Sulfur Compound Excess",
            category = "Oral & Lips",
            anatomicalRegion = "Posterior tongue dorsum and oral cavity",
            definition = "Noticeable unpleasant odor originating primarily from oral cavity microbial breakdown of protein substrates into volatile sulfur compounds (VSCs).",
            primarySymptoms = listOf(
                "Persistent unpleasant breath odor detectable to others or on dental floss",
                "Thick white or yellowish coating on posterior third of tongue",
                "Bitter, sour, or metallic morning taste",
                "Accompanied dry mouth and sticky oral sensation"
            ),
            commonCauses = listOf(
                "Anaerobic bacteria lurking in tongue papillae and deep periodontal pockets",
                "Food debris trapped between teeth from irregular flossing",
                "Xerostomia (saliva naturally neutralizes bacteria; lack of it spikes odor)",
                "Tonsil stones (caseous debris in tonsillar crypts) or post-nasal drip"
            ),
            atHomeCare = listOf(
                "Scrape posterior tongue dorsum with a stainless steel or plastic tongue cleaner",
                "Maintain religious interdental flossing daily",
                "Stay well hydrated with fresh water to stimulate saliva production",
                "Use a zinc-formulated or chlorine dioxide mouth rinse to neutralize sulfur bonds"
            ),
            redFlags = "Sweet fruity odor (diabetic ketoacidosis warning), ammonia-like breath (renal flag), or chronic foul odor with trouble swallowing.",
            preventionTip = "Clean the back of your tongue every morning before breakfast."
        ),
        MedicalTerm(
            id = "eczema",
            name = "Eczema (Atopic Dermatitis)",
            alternativeName = "Atopic Eczematous Flare",
            category = "Skin & Face",
            anatomicalRegion = "Flexural folds (elbows/knees), hands, neck, face",
            definition = "Chronic, pruritic, relapsing inflammatory skin disease characterized by immune dysregulation and inherited epidermal barrier defects (e.g. filaggrin deficiency).",
            primarySymptoms = listOf(
                "Intense, persistent itching that worsens at night",
                "Dry, erythematous, rough, cracked skin patches",
                "Excoriations (scratch marks) with weeping or crusting during acute flares",
                "Lichenification (thickened, leathery skin) from chronic scratching"
            ),
            commonCauses = listOf(
                "Compromised stratum corneum allowing moisture loss and allergen penetration",
                "Irritants like harsh detergents, wool fabrics, hot showers, and synthetic fragrances",
                "Allergenic environmental triggers (dust mites, pollen, pet dander)",
                "Climatic extremes (dry winter indoor heating)"
            ),
            atHomeCare = listOf(
                "Take brief (5-10 min) lukewarm showers; never use steaming hot water",
                "Apply thick ceramide ointment or rich cream within 3 minutes of bathing",
                "Use OTC 1% hydrocortisone cream on active itchy plaques for limited durations",
                "Wear soft 100% breathable cotton garments; avoid scratchy wool",
                "Keep fingernails short to prevent secondary skin breaks"
            ),
            redFlags = "Sudden eruption of painful clustered blisters (eczema herpeticum), extensive honey-colored crusts, or fever.",
            preventionTip = "Moisturize proactively twice daily even when the skin appears clear to sustain barrier defense."
        )
    )

    val homeRemedies: List<HomeRemedy> = listOf(
        HomeRemedy(
            id = "petrolatum",
            name = "White Petrolatum (Aquaphor / Vaseline)",
            category = "Skin",
            activeIngredient = "100% Pure USP White Petrolatum (or w/ Mineral Oil, Ceresin, Panthenol)",
            indications = listOf(
                "Chapped, cracked, or peeling lips",
                "Severely dry stratum corneum and skin barrier fissures",
                "Minor cuts, burns, and post-procedural wound healing",
                "Prevention of skin chafing and friction burns"
            ),
            mechanismOfAction = "The gold standard occlusive agent. Forms an impenetrable physical hydrophobic shield reducing Trans-Epidermal Water Loss (TEWL) by over 98%, allowing underlying skin cells to migrate and repair.",
            applicationSteps = listOf(
                "Cleanse the targeted zone with lukewarm water or a gentle cleanser.",
                "Ensure skin is slightly damp (petrolatum traps existing moisture).",
                "Warm a pea-sized amount between clean fingertips.",
                "Smooth an even layer over lips or dry skin patches; reapply 3-5 times daily."
            ),
            precautions = listOf(
                "Non-comedogenic on lips and body, but can trap sebum if applied over heavily acne-prone facial T-zones.",
                "External use only; avoid inhaling into respiratory tract."
            ),
            clinicalPearls = "Slugging: Applying petrolatum over hyaluronic acid or ceramide cream at bedtime supercharges epidermal recovery overnight."
        ),
        HomeRemedy(
            id = "hydrocortisone",
            name = "Hydrocortisone 1% Cream / Ointment",
            category = "First Aid",
            activeIngredient = "Hydrocortisone 1.0% (Low-potency Class 7 topical corticosteroid)",
            indications = listOf(
                "Mild allergic contact dermatitis and itchy razor burn flares",
                "Insect bite inflammation and localized redness",
                "Acute mild eczema flare-ups"
            ),
            mechanismOfAction = "Suppresses phospholipase A2 and decreases inflammatory cytokine cascades, rapidly mitigating microvascular dilation, edema, erythema, and itching.",
            applicationSteps = listOf(
                "Wash hands thoroughly prior to use.",
                "Apply a paper-thin fingertip unit directly to the affected inflamed area only.",
                "Gently rub in until absorbed twice daily for a maximum of 5 to 7 consecutive days.",
                "Wash hands immediately after application."
            ),
            precautions = listOf(
                "Do NOT use continuously for >7 days without medical supervision to prevent skin thinning (atrophy) or telangiectasias.",
                "Do NOT apply to open infected wounds, fungal infections (tinea), or acne vulgaris.",
                "Avoid eyelids and periocular areas."
            ),
            clinicalPearls = "Ointment formulation penetrates deeper than cream and causes less stinging on cracked, dry skin."
        ),
        HomeRemedy(
            id = "salicylate_aloe",
            name = "Salicylic Acid 1-2% & Pure Aloe Vera Gel",
            category = "Skin",
            activeIngredient = "Salicylic Acid (Beta-Hydroxy Acid) & Aloe Barbadensis Leaf Juice",
            indications = listOf(
                "Post-barber shave bumps (pseudofolliculitis)",
                "Keratosis pilaris and rough follicular clogs",
                "Soothing solar or razor irritation"
            ),
            mechanismOfAction = "Salicylic acid is lipid-soluble, penetrating deep into sebaceous follicles to dissolve keratin plugs and dead corneocytes. Aloe vera provides acemannan polysaccharides that calm inflammatory erythema.",
            applicationSteps = listOf(
                "After shaving or warm shower, pat area dry.",
                "Apply salicylic acid toner/serum onto razor-prone zones; allow 60 seconds to absorb.",
                "Follow immediately with a layer of pure cold-pressed aloe vera gel to soothe and hydrate."
            ),
            precautions = listOf(
                "Do not use if allergic to aspirin (acetylsalicylic acid).",
                "Can cause mild initial stinging on fresh razor nicks; always patch-test."
            ),
            clinicalPearls = "Keep pure aloe vera gel refrigerated for an immediate vasoconstrictive, cooling sensation on inflamed razor burn."
        ),
        HomeRemedy(
            id = "electrolytes_ors",
            name = "Oral Rehydration Salts (ORS) & Electrolytes",
            category = "Digestion",
            activeIngredient = "Sodium Chloride, Potassium Chloride, Trisodium Citrate, Glucose",
            indications = listOf(
                "Mild-to-moderate systemic dehydration",
                "Fluid depletion from heat exposure or strenuous athletics",
                "Rapid recovery from diarrhea or gastroenteritis"
            ),
            mechanismOfAction = "Harnesses the intestinal sodium-glucose cotransport mechanism (SGLT-1). Glucose facilitates rapid transport of sodium and water across the intestinal brush border into circulation, rehydrating cells 3x faster than plain water alone.",
            applicationSteps = listOf(
                "Dissolve 1 standard ORS packet (or 1/2 tsp salt + 6 level tsp sugar) in 1 liter of clean potable water.",
                "Stir until completely dissolved.",
                "Sip 100-200 ml every 15-20 minutes until urine becomes pale clear."
            ),
            precautions = listOf(
                "Do NOT add extra salt or mix with fruit juice/soda, as hypertonic solutions can worsen diarrhea.",
                "Patients with congestive heart failure or severe kidney disease should consult their physician regarding fluid/sodium intake."
            ),
            clinicalPearls = "Plain water in large amounts dilutes blood sodium. Balanced electrolyte solutions prevent water intoxication and hyponatremia."
        ),
        HomeRemedy(
            id = "zinc_oxide",
            name = "Zinc Oxide 20-40% Barrier Paste",
            category = "Skin",
            activeIngredient = "Zinc Oxide USP (20% - 40%) in petrolatum base",
            indications = listOf(
                "Severe skin chafing (groin, inner thighs, underarms)",
                "Moisture-associated skin damage and perlèche (lip corner fissures)",
                "Minor thermal and friction burns"
            ),
            mechanismOfAction = "Provides an inert, thick waterproof barrier that protects irritated skin from enzymatic damage and maceration, while delivering mild astringent and antiseptic properties.",
            applicationSteps = listOf(
                "Cleanse the chafed area and gently pat 100% dry.",
                "Apply an opaque, thick coat over the vulnerable surface.",
                "Reapply after sweating or showering."
            ),
            precautions = listOf(
                "Leaves a noticeable white cast (best applied under clothing or before bedtime).",
                "Can stain clothing; allow time to settle."
            ),
            clinicalPearls = "Superb for angular cheilitis at night: seals the mouth corners against nocturnal saliva pooling."
        ),
        HomeRemedy(
            id = "saline_rinse",
            name = "Warm Saline Oral Rinse",
            category = "Mouth/Lips",
            activeIngredient = "Sodium Chloride (0.9% Isotonic or 2-3% Hypertonic Solution)",
            indications = listOf(
                "Gingival bleeding and tender gum inflammation",
                "Sore throat mucosal irritation",
                "Aphthous ulcers (canker sores) and post-flossing discomfort"
            ),
            mechanismOfAction = "Hypertonic or isotonic warm saline creates an osmotic pressure gradient that draws excess interstitial edema fluid out of swollen tissues, while clearing debris and inhibiting anaerobic bacteria.",
            applicationSteps = listOf(
                "Dissolve 1/2 teaspoon of table salt into 8 oz (1 cup) of warm water.",
                "Swish gently around teeth and gums for 30-45 seconds.",
                "Spit out completely into sink; do not swallow.",
                "Repeat 2 to 3 times daily after meals."
            ),
            precautions = listOf(
                "Do not swallow saline to avoid excess sodium ingestion.",
                "Water should be comfortably warm, never hot to prevent mucosal thermal injury."
            ),
            clinicalPearls = "Unlike alcohol mouthwashes, saline does not kill friendly oral microbiome or dry out mucous membranes."
        ),
        HomeRemedy(
            id = "colloidal_oatmeal",
            name = "Colloidal Oatmeal Bath & Cream",
            category = "Skin",
            activeIngredient = "Avena Sativa (Oat) Kernel Flour",
            indications = listOf(
                "Generalized skin pruritus (itching) and eczema flares",
                "Dry, irritable winter skin and sunburn discomfort",
                "Hives or contact rash soothing"
            ),
            mechanismOfAction = "Contains avenanthramides (potent anti-inflammatory polyphenols) and beta-glucans that coat the skin, suppress histamine release, and attract moisture to fragile epidermal barriers.",
            applicationSteps = listOf(
                "For bath: Sprinkle 1 cup of micro-milled colloidal oatmeal under running warm tap water; stir until milky.",
                "Soak for 10-15 minutes.",
                "Pat skin lightly with towel (do not scrub dry).",
                "For cream: Smooth directly over itchy dry skin twice daily."
            ),
            precautions = listOf(
                "Oat baths make tub surfaces extremely slippery; enter and exit with care.",
                "Ensure no known allergy to oat grains."
            ),
            clinicalPearls = "A soothing colloidal oatmeal bath before bed dramatically reduces the nocturnal itch-scratch cycle in eczema sufferers."
        ),
        HomeRemedy(
            id = "ceramide_complex",
            name = "Ceramide 1, 3, 6-II Barrier Repair Lotion",
            category = "Skin",
            activeIngredient = "Identical Ceramide Complex + Cholesterol + Fatty Acids (3:1:1 ratio)",
            indications = listOf(
                "Impaired stratum corneum barrier",
                "Flaking facial skin and dry peeling areas",
                "Post-cleanser tightness and barrier protection"
            ),
            mechanismOfAction = "Replenishes the intercellular 'mortar' of the stratum corneum lipid bilayer, directly restoring physiological barrier resilience and preventing irritant penetration.",
            applicationSteps = listOf(
                "Apply immediately after washing while skin retains slight hydration.",
                "Massage a dime-to-quarter-sized amount using upward strokes.",
                "Use morning and evening as your staple base moisturizer."
            ),
            precautions = listOf(
                "Choose fragrance-free formulations labeled for sensitive/atopic skin."
            ),
            clinicalPearls = "The skin barrier is composed of 50% ceramides, 25% cholesterol, and 15% free fatty acids; look for products providing this multi-lipid harmony."
        )
    )

    fun assessSymptoms(request: AssessmentRequest): AssessmentResult {
        val selected = request.selectedSymptoms
        val matches = mutableListOf<ConditionMatch>()

        // 1. Cheilitis / Lip Dryness Check
        if (selected.contains("scaling_lips") || (selected.contains("cracked_corners") && request.affectedRegion.contains("Lip", ignoreCase = true))) {
            val isAngular = selected.contains("cracked_corners")
            matches.add(
                ConditionMatch(
                    conditionName = if (isAngular) "Angular Cheilitis" else "Cheilitis (Lip Barrier Disruption)",
                    alternativeName = if (isAngular) "Perlèche" else "Severe Chapped Lips",
                    matchRelevance = "High Match",
                    whyMatched = "Symptoms match compromised vermilion barrier with scaling, fissuring, and peeling in the lip region.",
                    firstLineActions = listOf(
                        "Apply generous 100% white petrolatum (Aquaphor/Vaseline) 5-6 times daily",
                        "Slather a thick slugging coat before bed",
                        "If lip corners have deep cracks, apply 1% clotrimazole + zinc oxide paste twice daily"
                    ),
                    thingsToAvoid = listOf(
                        "Strictly stop licking lips (saliva enzymes digest delicate lip tissue)",
                        "Avoid balms containing camphor, menthol, phenol, or artificial cinnamon flavors",
                        "Do not pick or pull peeling skin flaps"
                    ),
                    redFlagSigns = "Persistent non-healing ulcer beyond 2 weeks, bleeding crusts that do not improve, or painful spreading swelling."
                )
            )
        }

        // 2. Folliculitis / Razor Bumps
        if (selected.contains("razor_irritation") || (selected.contains("skin_bumps") && request.affectedRegion.contains("Beard", ignoreCase = true))) {
            matches.add(
                ConditionMatch(
                    conditionName = "Pseudofolliculitis Barbae (Razor Bumps)",
                    alternativeName = "Ingrown Hairs / Post-Shave Folliculitis",
                    matchRelevance = "High Match",
                    whyMatched = "Curled hair shafts piercing the follicle wall following shaving, resulting in inflamed papules and bumps.",
                    firstLineActions = listOf(
                        "Pause close blade shaving for 48-72 hours to allow hairs to emerge",
                        "Apply warm damp compresses for 5 minutes twice daily to soften hair follicles",
                        "Apply cold-pressed aloe vera gel or 1% hydrocortisone cream for acute red flare-ups"
                    ),
                    thingsToAvoid = listOf(
                        "Do NOT shave against the grain or stretch skin tight",
                        "Do NOT pluck ingrown hairs with tweezers (creates scarred channels)",
                        "Avoid alcohol-based aftershave splashes that burn and irritate"
                    ),
                    redFlagSigns = "Large throbbing boils, draining pus, or fever (signs of bacterial abscess requiring antibiotics)."
                )
            )
        }

        // 3. Contact Dermatitis / Skin Irritation
        if (selected.contains("redness") || selected.contains("itching") || selected.contains("excessive_dryness")) {
            matches.add(
                ConditionMatch(
                    conditionName = "Contact Dermatitis or Barrier Irritation",
                    alternativeName = "Eczematous Skin Irritation",
                    matchRelevance = if (selected.contains("itching") && selected.contains("redness")) "High Match" else "Moderate Match",
                    whyMatched = "Cutaneous inflammation manifesting as redness, pruritus (itching), or stinging barrier compromise.",
                    firstLineActions = listOf(
                        "Wash with lukewarm water and mild fragrance-free synthetic soap-free cleanser",
                        "Apply cold compresses for 10 minutes to soothe acute burning/itching",
                        "Seal skin with rich ceramide cream or pure petrolatum immediately after washing"
                    ),
                    thingsToAvoid = listOf(
                        "Avoid essential oils, fragrances, alcohol astringents, and exfoliating scrubs",
                        "Do NOT take hot showers which strip protective lipid layers",
                        "Do not scratch or rub skin vigorously"
                    ),
                    redFlagSigns = "Facial swelling affecting eyelids/lips, weeping yellow crusts, or hives spreading across whole body."
                )
            )
        }

        // 4. Seborrheic Dermatitis
        if (selected.contains("flaking_patches") || (selected.contains("redness") && request.affectedRegion.contains("Scalp", ignoreCase = true))) {
            matches.add(
                ConditionMatch(
                    conditionName = "Seborrheic Dermatitis",
                    alternativeName = "Scalp / Beard Seborrhea",
                    matchRelevance = "Moderate Match",
                    whyMatched = "Flaking and scaling concentrated in sebum-dense areas such as scalp hairline or beard.",
                    firstLineActions = listOf(
                        "Use medicated shampoo containing 1-2% zinc pyrithione, ketoconazole, or selenium sulfide",
                        "Leave medicated foam on skin/hair for 3-5 minutes before rinsing",
                        "Hydrate skin with lightweight non-comedogenic squalane or ceramide moisturizer"
                    ),
                    thingsToAvoid = listOf(
                        "Avoid heavy plant oils like coconut oil or olive oil which nourish Malassezia yeast",
                        "Avoid scratching scales aggressively to prevent excoriation"
                    ),
                    redFlagSigns = "Secondary bacterial skin infection, bleeding plaques, or ear canal swelling."
                )
            )
        }

        // 5. Xerostomia / Oral Dehydration
        if (selected.contains("dry_mouth") || selected.contains("dark_urine") || selected.contains("headache_fatigue")) {
            matches.add(
                ConditionMatch(
                    conditionName = "Xerostomia & Dehydration Deficit",
                    alternativeName = "Salivary Hypofunction / Fluid Depletion",
                    matchRelevance = "High Match",
                    whyMatched = "Mucosal dryness and sticky oral cavity accompanied by fluid balance deficits.",
                    firstLineActions = listOf(
                        "Sip Oral Rehydration Salts (ORS) or structured electrolyte water throughout the day",
                        "Chew 100% xylitol gum to mechanically trigger natural saliva release",
                        "Use alcohol-free lubricating oral rinse and bedside cool mist humidifier"
                    ),
                    thingsToAvoid = listOf(
                        "Avoid alcohol-based mouth rinses and excessive caffeine",
                        "Avoid salty, dry, or overly acidic foods that aggravate dry mucosa"
                    ),
                    redFlagSigns = "Inability to swallow liquids, dizziness on standing, or zero urine output over 8 hours."
                )
            )
        }

        // 6. Gingivitis
        if (selected.contains("bleeding_gums")) {
            matches.add(
                ConditionMatch(
                    conditionName = "Gingivitis",
                    alternativeName = "Early Periodontal Inflammation",
                    matchRelevance = "High Match",
                    whyMatched = "Gingival bleeding upon probing, flossing, or brushing indicates bacterial plaque buildup along gumline.",
                    firstLineActions = listOf(
                        "Continue flossing gently daily; bleeding resolves in 7-10 days as tissue heals",
                        "Swish with warm salt water (1/2 tsp salt in 1 cup warm water) for 30s after meals",
                        "Use extra-soft toothbrush bristles angled 45 degrees toward gums"
                    ),
                    thingsToAvoid = listOf(
                        "Do NOT stop flossing because gums bleed (stopping allows bacteria to multiply)",
                        "Avoid aggressive scrubbing with hard bristles that erode enamel and gums"
                    ),
                    redFlagSigns = "Loose teeth, severe throbbing jaw pain, or purulent pus drainage from gums."
                )
            )
        }

        // Fallback match if user selected general items
        if (matches.isEmpty()) {
            matches.add(
                ConditionMatch(
                    conditionName = "General Cutaneous & Mucosal Stress",
                    alternativeName = "Mild Tissue Barrier Irritation",
                    matchRelevance = "Potential Match",
                    whyMatched = "Selected symptoms indicate early irritation or environmental dehydration.",
                    firstLineActions = listOf(
                        "Maintain steady hydration with at least 2.5L of water daily",
                        "Apply bland fragrance-free barrier ointments (petrolatum or ceramides)",
                        "Rest the affected zone and protect from sun and harsh weather"
                    ),
                    thingsToAvoid = listOf(
                        "Avoid harsh chemical soaps, alcohol products, and physical scrubbing"
                    ),
                    redFlagSigns = "Fever, rapidly spreading redness, or severe pain."
                )
            )
        }

        val generalCare = listOf(
            "Hydrate the stratum corneum: apply bland moisturizers within 3 minutes of washing.",
            "Protect vulnerable areas with pure white petrolatum to lock in moisture.",
            "Maintain baseline water intake (2.5L/day) with balanced electrolytes if dehydrated.",
            "Pause cosmetic exfoliants, retinoids, fragrances, and astringents during flare-ups."
        )

        val commonTriggers = listOf(
            "Licking chapped lips (enzymes break down epidermal lipids).",
            "Shaving against hair grain or using dull multi-blade razors.",
            "Steaming hot showers that melt essential intercellular skin ceramides.",
            "Alcohol-based facial splashes and synthetic fragrances."
        )

        val urgentWarning = if (request.duration == "> 1 week" || selected.size >= 4) {
            "Note: Symptoms lasting over 7 days or involving multiple bodily regions warrant evaluation by a board-certified dermatologist or healthcare provider to rule out underlying systemic or microbial conditions."
        } else null

        return AssessmentResult(
            primaryMatches = matches,
            generalFirstLineCare = generalCare,
            commonTriggersToAvoid = commonTriggers,
            urgentCareWarning = urgentWarning
        )
    }
}
