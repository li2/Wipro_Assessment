package me.li2.android.architecture.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.li2.android.architecture.ui.articledetail.view.ArticleDetailFragment
import me.li2.android.architecture.ui.articles.view.ArticlesActivity
import me.li2.android.architecture.ui.articles.view.ArticlesFragment

/**
 * This is a given module to dagger.
 * We map ALL our activities here, then Dagger knows our activities in compile time.
 * Otherwise it causes IllegalArgumentException: No injector factory bound for Class
 *
 * @author Weiyi Li on 31/3/18 | https://github.com/li2
 */

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [ArticleDetailModule::class])
    internal abstract fun bindArticleDetailFragment(): ArticleDetailFragment

    @ContributesAndroidInjector
    internal abstract fun bindArticlesActivity(): ArticlesActivity

    @ContributesAndroidInjector(modules = [ArticlesModule::class])
    @ArticlesScope
    internal abstract fun bindArticlesFragment(): ArticlesFragment
}
