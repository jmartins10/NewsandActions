package com.example.newsandactions.Retrofit

import android.os.Handler
import android.os.Looper
import com.example.newsandactions.MainActivity
import com.example.newsandactions.NewsFragmentActivity
import com.example.newsandactions.entities.Image
import com.example.newsandactions.entities.News
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsRetrofit(val activityMainBinding: MainActivity) {

    private val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://workgaddammit.azurewebsites.net/api/")
        .build()
        .create(RetrofitInterface::class.java)

    fun getByCat(category: String) {
        val call = when (category) {
            "Desporto" -> retrofitBuilder.getSport()
            "Economia" -> retrofitBuilder.getEconomic()
            "Política" -> retrofitBuilder.getPolitics()
            "Mundo" -> retrofitBuilder.getWorld()
            "Ações" -> retrofitBuilder.getActions()
            else -> retrofitBuilder.getLatest()
        }

        call.enqueue(object : Callback<List<News?>> {
            override fun onResponse(call: Call<List<News?>>, response: Response<List<News?>>) {
                val list = if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    response.body()!!.filterNotNull().take(8)
                } else {
                    getMockNews(category)
                }
                populateCategory(category, list)
            }

            override fun onFailure(call: Call<List<News?>>, t: Throwable) {
                // Fallback to mock data when network/server fails
                populateCategory(category, getMockNews(category))
            }
        })
    }

    fun getLatest() {
        retrofitBuilder.getLatest().enqueue(object : Callback<List<News?>> {
            override fun onResponse(call: Call<List<News?>>, response: Response<List<News?>>) {
                val list = if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    response.body()!!.filterNotNull().take(8)
                } else {
                    getMockNews("Últimas")
                }
                populateCategory("Últimas", list)
                activityMainBinding.setUpRetro()
            }

            override fun onFailure(call: Call<List<News?>>, t: Throwable) {
                populateCategory("Últimas", getMockNews("Últimas"))
                activityMainBinding.setUpRetro()
            }
        })
    }

    private fun populateCategory(category: String, list: List<News>) {
        Handler(Looper.getMainLooper()).post {
            val layout = when (category) {
                "Últimas" -> NewsFragmentActivity.SaveMe.latestNewsLayout
                "Desporto" -> NewsFragmentActivity.SaveMe.sportNewsLayout
                "Economia" -> NewsFragmentActivity.SaveMe.economyNewsLayout
                "Política" -> NewsFragmentActivity.SaveMe.politicsNewsLayout
                "Mundo" -> NewsFragmentActivity.SaveMe.worldNewsLayout
                "Ações" -> NewsFragmentActivity.SaveMe.actionsNewsLayout
                else -> NewsFragmentActivity.SaveMe.latestNewsLayout
            }
            layout.removeAllViews()
            layout.addView(NewsFragmentActivity.SaveMe.createCardLayout(list))

            activityMainBinding.hideLoading()
        }
    }

    private fun getMockNews(category: String): List<News> {
        return when (category) {
            "Últimas" -> listOf(
                News(null, null, null, null, null, "Tecnologia", listOf("Últimas"),
                    "A inteligência artificial e as novas tecnologias continuam a transformar os vários setores industriais em 2025.",
                    "Inovações na Inteligência Artificial impulsionam mercado global.",
                    "1", "1", Image("https://picsum.photos/800/400?random=1", "jpeg"), null, "Hoje, 10:30", "Avanços na Inteligência Artificial em 2025"),
                News(null, null, null, null, null, "Economia", listOf("Últimas"),
                    "Os principais índices bolsistas europeus registaram hoje uma sessão de fortes ganhos motivada por dados económicos positivos.",
                    "Mercados financeiros em alta com novos relatórios positivos.",
                    "2", "2", Image("https://picsum.photos/800/400?random=2", "jpeg"), null, "Hoje, 09:15", "Mercados Financeiros Globais em Recuperação"),
                News(null, null, null, null, null, "Geral", listOf("Últimas"),
                    "O novo centro cultural abriu hoje as portas ao público com uma exposição dedicada à arte contemporânea.",
                    "Inauguração do novo centro de artes atrai centenas de visitantes.",
                    "3", "3", Image("https://picsum.photos/800/400?random=3", "jpeg"), null, "Ontem, 18:45", "Inauguração do Novo Centro Cultural Nacional")
            )
            "Desporto" -> listOf(
                News(null, null, null, null, null, "Desporto", listOf("Desporto"),
                    "Num jogo intenso do início ao fim, a equipa da casa conseguiu a vitória no último minuto de jogo.",
                    "Grande vitória emocionante marca o topo da classificação.",
                    "4", "4", Image("https://picsum.photos/800/400?random=4", "jpeg"), null, "Hoje, 22:00", "Vitória Decisiva no Campeonato Nacional de Futebol"),
                News(null, null, null, null, null, "Desporto", listOf("Desporto"),
                    "A atleta portuguesa conquistou este fim de semana a medalha de ouro na competição europeia de atletismo.",
                    "Atleta nacional estabelece novo recorde europeu.",
                    "5", "5", Image("https://picsum.photos/800/400?random=5", "jpeg"), null, "Hoje, 16:20", "Medalha de Ouro no Campeonato Europeu de Atletismo"),
                News(null, null, null, null, null, "Desporto", listOf("Desporto"),
                    "A pré-época de basquetebol começou com os clubes a apresentarem os seus novos reforços internacionais.",
                    "Equipas preparam nova temporada com ambições elevadas.",
                    "6", "6", Image("https://picsum.photos/800/400?random=6", "jpeg"), null, "Ontem, 14:10", "Abertura da Nova Época de Basquetebol")
            )
            "Economia" -> listOf(
                News(null, null, null, null, null, "Economia", listOf("Economia"),
                    "A taxa de inflação voltou a descer este mês, situando-se abaixo da meta estabelecida pelas autoridades monetárias.",
                    "Taxa de inflação cai para os níveis mais baixos dos últimos anos.",
                    "7", "7", Image("https://picsum.photos/800/400?random=7", "jpeg"), null, "Hoje, 11:00", "Inflação Continua a Descer no Último Trimestre"),
                News(null, null, null, null, null, "Economia", listOf("Economia"),
                    "As exportações das empresas nacionais registaram um crescimento superior a 12% em comparação com o período homólogo.",
                    "Exportações impulsionam o crescimento do PIB nacional.",
                    "8", "8", Image("https://picsum.photos/800/400?random=8", "jpeg"), null, "Hoje, 08:30", "Exportações Nacionais Atingem Valor Recorde"),
                News(null, null, null, null, null, "Economia", listOf("Economia"),
                    "O Governo apresentou uma nova linha de financiamento a taxa reduzida para pequenas e médias empresas em expansão.",
                    "Novo programa de apoio ao empreendedorismo foi hoje lançado.",
                    "9", "9", Image("https://picsum.photos/800/400?random=9", "jpeg"), null, "Ontem, 17:00", "Incentivos para Pequenas e Médias Empresas")
            )
            "Política" -> listOf(
                News(null, null, null, null, null, "Política", listOf("Política"),
                    "O parlamento aprovou hoje a versão final do orçamento e das novas medidas de apoio social.",
                    "Proposta orçamental aprovada com votos a favor da maioria.",
                    "10", "10", Image("https://picsum.photos/800/400?random=10", "jpeg"), null, "Hoje, 19:30", "Assembleia Aprova Pacote de Medidas Sociais"),
                News(null, null, null, null, null, "Política", listOf("Política"),
                    "Os líderes europeus reuniram-se em Bruxelas para discutir a transição energética e a segurança na região.",
                    "Cimeira internacional foca-se na sustentabilidade energética.",
                    "11", "11", Image("https://picsum.photos/800/400?random=11", "jpeg"), null, "Hoje, 15:45", "Líderes Europeus Reunidos em Cimeira Internacional"),
                News(null, null, null, null, null, "Política", listOf("Política"),
                    "Foi apresentada a proposta de reforma legislativa para simplificar o acesso aos serviços públicos de saúde.",
                    "Iniciativa visa reduzir tempos de espera e otimizar recursos.",
                    "12", "12", Image("https://picsum.photos/800/400?random=12", "jpeg"), null, "Ontem, 12:00", "Reforma na Saúde em Consulta Pública")
            )
            "Mundo" -> listOf(
                News(null, null, null, null, null, "Mundo", listOf("Mundo"),
                    "Representantes de mais de 50 nações assinaram um acordo histórico para a proteção dos oceanos e biodiversidade.",
                    "Acordo climático global estabelece novas metas rigorosas.",
                    "13", "13", Image("https://picsum.photos/800/400?random=13", "jpeg"), null, "Hoje, 20:10", "Acordo Histórico sobre Preservação Ambiental"),
                News(null, null, null, null, null, "Mundo", listOf("Mundo"),
                    "A nova sonda espacial captou imagens sem precedentes da superfície do planeta vermelho.",
                    "Missão científica envia imagens inéditas do espaço profundo.",
                    "14", "14", Image("https://picsum.photos/800/400?random=14", "jpeg"), null, "Hoje, 13:25", "Novas Imagens do Espaço Reveladas pela NASA"),
                News(null, null, null, null, null, "Mundo", listOf("Mundo"),
                    "Iniciativas transfronteiriças reforçam a cooperação económica e cultural entre países vizinhos.",
                    "Parcerias internacionais trazem novos investimentos comunitários.",
                    "15", "15", Image("https://picsum.photos/800/400?random=15", "jpeg"), null, "Ontem, 10:50", "Cooperação Internacional Reforçada na Europa")
            )
            "Ações" -> listOf(
                News(null, null, null, null, null, "Euronext Lisbon", listOf("Ações"),
                    "O principal índice da Bolsa de Lisboa (PSI) encerrou a sessão em alta de +0,92%, impulsionado pelo desempenho do setor bancário e energético. Destaque para os ganhos do BCP (+2,85%) e da Galp (+2,10%).",
                    "Cotação: 6 785,40 pts | Variação: +61,90 pts (+0,92%) | Volume: 42,5M€",
                    "16", "16", Image("https://picsum.photos/800/400?random=101", "jpeg"), null, "Hoje, 17:35 - Fecho", "PSI (Bolsa de Lisboa) — 6 785,40 pts (+0,92%)"),
                News(null, null, null, null, null, "BCP.LS", listOf("Ações"),
                    "O Banco Comercial Português destacou-se com fortes ganhos na sessão de hoje, reagindo às perspetivas otimistas de margem financeira e melhoria das cotações no setor bancário europeu.",
                    "Cotação: 0,385 € | Variação: +0,011 € (+2,85%) | Volume: 24,5M títulos",
                    "17", "17", Image("https://picsum.photos/800/400?random=102", "jpeg"), null, "Hoje, 17:30", "BCP (BCP.LS) — 0,385 € (+2,85%)"),
                News(null, null, null, null, null, "GALP.LS", listOf("Ações"),
                    "A Galp Energia liderou as subidas entre as grandes cotadas portuguesas, impulsionada pela valorização do crude nos mercados internacionais e avanços na transição para energias de baixo carbono.",
                    "Cotação: 16,150 € | Variação: +0,332 € (+2,10%) | Volume: 1,9M títulos",
                    "18", "18", Image("https://picsum.photos/800/400?random=103", "jpeg"), null, "Hoje, 17:30", "Galp Energia (GALP.LS) — 16,150 € (+2,10%)"),
                News(null, null, null, null, null, "EDP.LS", listOf("Ações"),
                    "As ações da EDP - Energias de Portugal registaram valorização positiva após a apresentação dos dados de produção de energia renovável do primeiro semestre.",
                    "Cotação: 3,820 € | Variação: +0,055 € (+1,45%) | Volume: 3,8M títulos",
                    "19", "19", Image("https://picsum.photos/800/400?random=104", "jpeg"), null, "Hoje, 17:30", "EDP (EDP.LS) — 3,820 € (+1,45%)"),
                News(null, null, null, null, null, "JMT.LS", listOf("Ações"),
                    "A dona do Pingo Doce e da Biedronka registou uma ligeira variação técnica, mantendo a consolidação em torno do nível dos 18,40€.",
                    "Cotação: 18,420 € | Variação: -0,120 € (-0,65%) | Volume: 850K títulos",
                    "20", "20", Image("https://picsum.photos/800/400?random=105", "jpeg"), null, "Hoje, 17:30", "Jerónimo Martins (JMT.LS) — 18,420 € (-0,65%)"),
                News(null, null, null, null, null, "NVG.LS", listOf("Ações"),
                    "A Navigator Company subiu com o aumento da procura global de pastas de papel e embalagens sustentáveis na Europa.",
                    "Cotação: 3,680 € | Variação: +0,030 € (+0,82%) | Volume: 1,2M títulos",
                    "21", "21", Image("https://picsum.photos/800/400?random=106", "jpeg"), null, "Hoje, 17:30", "The Navigator Company (NVG.LS) — 3,680 € (+0,82%)"),
                News(null, null, null, null, null, "EGL.LS", listOf("Ações"),
                    "A construtora Mota-Engil registou um ganho de mais de 3% após a adjudicação de novos projetos de infraestruturas internacionais.",
                    "Cotação: 2,580 € | Variação: +0,080 € (+3,20%) | Volume: 2,1M títulos",
                    "22", "22", Image("https://picsum.photos/800/400?random=107", "jpeg"), null, "Hoje, 17:30", "Mota-Engil (EGL.LS) — 2,580 € (+3,20%)")
            )
            else -> emptyList()
        }
    }
}
